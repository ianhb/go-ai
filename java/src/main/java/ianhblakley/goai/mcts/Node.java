package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.*;
import ianhblakley.goai.neuralnetworkconnection.NeuralNetworkClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds the state of a game in the MCT
 * Represents a node in a tree
 *
 * Created by ian on 10/17/16.
 */
class Node {

    private static final Logger logger = LogManager.getFormatterLogger(Node.class);
    private static final Random random = new Random(System.currentTimeMillis());

    private final Node parent;
    private final Set<Node> children;
    private final Set<Position> possibleChildren;
    private final Position move;
    private final PositionState color;
    private final Board state;
    private final boolean terminalState;
    private final AtomicInteger wins;
    private final AtomicInteger plays;
    private Set<ValuedBoard> movesAsBoards;
    private float value;

    Node(Node parent, Board b, Position move, PositionState color) {
        this.parent = parent;
        this.children = new HashSet<>();
        this.color = color;
        this.state = b.deepCopy();
        wins = new AtomicInteger(0);
        plays = new AtomicInteger(0);
        this.move = move;
        if (move != null) {
            state.placeMove(new Move(move, Utils.getOppositeColor(color)));
        }
        this.possibleChildren = new HashSet<>(state.getAvailableSpaces());
        possibleChildren.removeIf(o -> !StateChecker.isLegalMove(new Move(o, color), state));
        if (possibleChildren.size() < 100) {
            possibleChildren.add(null);
        }
        //this.possibleChildren.add(null);
        // TODO: add pass as a possible child
        this.terminalState = possibleChildren.size() == 0;
    }

    Position getMove() {
        return move;
    }

    Set<Node> getChildren() {
        return children;
    }

    /**
     * Records a win found by simulation of this or child node
     */
    void logWin() {
        wins.incrementAndGet();
        plays.incrementAndGet();
    }

    /**
     * Records a loss found by simulation of this or child node
     */
    void logLoss() {
        wins.decrementAndGet();
        plays.incrementAndGet();
    }

    /**
     * Returns a node that represents a random, unvisited child.
     * Found by randomly selecting from the {@link #getPossibleChildren()}
     *
     * @return a random, unvisited node
     */
    Node selectNewRandomChild() {
        Position randomMove = randomSelect(getPossibleChildren());
        if (randomMove == null) {
            return this;
        }
        return addChild(randomMove);
    }

    Node selectHighestValueChild(NeuralNetworkClient client) {
        if (movesAsBoards == null) {
            movesAsBoards = new HashSet<>();
            createMovesAsBoards(client);
        }
        float highestValue = Float.NEGATIVE_INFINITY;
        ValuedBoard highestValueChild = null;
        for (ValuedBoard b : movesAsBoards) {
            if (b.getValue() >= highestValue) {
                highestValue = b.getValue();
                highestValueChild = b;
            }
        }
        assert highestValueChild != null;
        Node newChild = addChild(highestValueChild.getMove(), state.deepCopy());
        newChild.setValue(highestValueChild.getValue());
        movesAsBoards.remove(highestValueChild);
        return newChild;
    }

    private void createMovesAsBoards(NeuralNetworkClient client) {
        assert movesAsBoards.size() == 0;
        List<Position> positionList = new ArrayList<>(getPossibleChildren());
        List<Float> boardValues = client.getValues(color, state, positionList);
        assert boardValues.size() == positionList.size();
        for (int i = 0; i < boardValues.size(); i++) {
            movesAsBoards.add(new ValuedBoard(boardValues.get(i), positionList.get(i)));
        }
        assert movesAsBoards.size() == getPossibleChildren().size();
    }

    /**
     * Randomly selects a move from moves
     * @param moves set of possible moves
     * @return random move
     */
    private Position randomSelect(Set<Position> moves) {
        List<Position> openPositions = new ArrayList<>(moves);
        int randomInt = random.nextInt(openPositions.size());
        Position randomMove = openPositions.get(randomInt);
        if (randomMove != null) assert StateChecker.isLegalMove(new Move(randomMove, color), state);
        return randomMove;
    }

    Node getParent() {
        return parent;
    }

    /**
     * Returns set of all possible moves that haven't been visited yet
     * Once a child node is created, it is removed from this
     * @return set of all possible, unmade moves
     */
    private Set<Position> getPossibleChildren() {
        return possibleChildren;
    }

    /**
     * Returns a copy of the gameboard
     * @return copy of gameboard
     */
    Board getState() {
        return state.deepCopy();
    }

    boolean isNotFullyExpanded() {
        return possibleChildren.size() != 0;
    }

    boolean isNotTerminalState() {
        return !terminalState;
    }

    /**
     * Creates a child node that represents the move made by position.
     * position is removed from {@link #getPossibleChildren()} and the child is added
     * to {@link #getChildren()}
     * @param position move to create node for
     * @return node representing position made from current state
     */
    private Node addChild(Position position) {
        Board childState = state.deepCopy();
        return addChild(position, childState);
    }

    private Node addChild(Position position, Board board) {
        assert possibleChildren.size() > 0;
        assert possibleChildren.contains(position);
        possibleChildren.remove(position);
        Node child = new Node(this, board, position, Utils.getOppositeColor(color));
        children.add(child);
        return child;
    }

    /**
     * Returns the percentage of simulated games that started at this state that the player won
     *
     * @return wins / games of simulations below node
     */
    double getWinProbability() {
        if (plays.get() == 0) {
            return 0;
        }
        return (double) wins.get() / (double) plays.get();
    }

    int getPlays() {
        return plays.get();
    }

    float getValue() {
        return value;
    }

    private void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(getWinProbability());
    }

    private class ValuedBoard {
        private final float value;
        private final Position move;

        ValuedBoard(float value, Position move) {
            this.value = value;
            this.move = move;
        }

        float getValue() {
            return value;
        }

        Position getMove() {
            return move;
        }
    }
}

