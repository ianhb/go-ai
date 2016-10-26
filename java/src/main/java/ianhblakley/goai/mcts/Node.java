package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Holds the state of a game in the MCT
 * Represents a node in a tree
 *
 * Created by ian on 10/17/16.
 */
class Node {

    private static final Random random = new Random(System.currentTimeMillis());

    private final Node parent;
    private final Set<Node> children;
    private final Set<Position> possibleChildren;
    private final Position move;
    private final PositionState color;
    private final Board state;
    private final boolean terminalState;
    private int wins;
    private int plays;

    Node(Node parent, Board b, Position move, PositionState color) {
        this.parent = parent;
        this.children = new HashSet<>();
        this.possibleChildren = b.getLegalMoves(color);
        this.color = color;
        this.state = b.deepCopy();
        this.terminalState = b.isEndGame();
        wins = 0;
        plays = 0;
        this.move = move;
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
        wins++;
        plays++;
    }

    /**
     * Records a loss found by simulation of this or child node
     */
    void logLoss() {
        wins--;
        plays++;
    }

    /**
     * Returns a node that represents a random, unvisited child.
     * Found by randomly selecting from the {@link #getPossibleChildren()}
     *
     * @return a random, unvisited node
     */
    Node selectNewRandomChild() {
        Position randomMove = randomSelect(getPossibleChildren());
        return addChild(randomMove);
    }

    /**
     * Randomly selects a move from moves
     * @param moves set of possible moves
     * @return random move
     */
    private Position randomSelect(Set<Position> moves) {
        int size = moves.size();
        int randomInt = random.nextInt(size);
        int i = 0;
        for (Position p : moves) {
            if (i == randomInt) {
                return p;
            }
            i++;
        }
        return null;
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
        childState.placeMove(new Move(position, Utils.getOppositeColor(color)));
        Node child = new Node(this, childState, position, Utils.getOppositeColor(color));
        children.add(child);
        return child;
    }

    double getWinProbability() {
        if (plays == 0) {
            return 0;
        }
        return (double) wins / (double) plays;
    }

    int getPlays() {
        return plays;
    }

    @Override
    public String toString() {
        return String.valueOf(getWinProbability());
    }

}

