package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.*;
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

    private static final Random random = new Random(System.currentTimeMillis());
    private static final Logger logger = LogManager.getFormatterLogger(Node.class);

    private final Node parent;
    private final Set<Node> children;
    private final Set<Position> possibleChildren;
    private final Position move;
    private final PositionState color;
    private final Board state;
    private final boolean terminalState;
    private final AtomicInteger wins;
    private final AtomicInteger plays;

    Node(Node parent, Board b, Position move, PositionState color) {
        this.parent = parent;
        this.children = new HashSet<>();
        this.color = color;
        this.state = b.deepCopy();
        wins = new AtomicInteger(0);
        plays = new AtomicInteger(0);
        this.move = move;
        if (move != null) {
            state.placeMove(new Move(move, color));
        }
        this.possibleChildren = new HashSet<>(state.getAvailableSpaces());
        Iterator positionIterator = possibleChildren.iterator();
        while (positionIterator.hasNext()) {
            if (!StateChecker.isLegalMove(new Move((Position) positionIterator.next(), color), state)) {
                positionIterator.remove();
            }
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
        assert possibleChildren.size() > 0;
        assert possibleChildren.contains(position);
        possibleChildren.remove(position);
        Board childState = state.deepCopy();
        Node child = new Node(this, childState, position, Utils.getOppositeColor(color));
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

    @Override
    public String toString() {
        return String.valueOf(getWinProbability());
    }

}

