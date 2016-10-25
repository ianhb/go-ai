package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds the state of a game in the MCT
 * Represents a node in a tree
 *
 * Created by ian on 10/17/16.
 */
class Node {

    private final Node parent;
    private final Set<Node> children;
    private final Set<Position> possibleChildren;
    private Position move;
    private final PositionState color;
    private final Board state;
    private final boolean terminalState;
    private int wins;
    private int plays;

    Node(Node parent, Board b, Position move, PositionState color) {
        this(parent, b, color);
        this.move = move;
        children.add(new Node(this, b.deepCopy(), Utils.getOppositeColor(color)));
    }

    private Node(Node parent, Board b, PositionState color) {
        this.parent = parent;
        this.children = new HashSet<>();
        this.possibleChildren = b.legalMoves(color);
        this.color = color;
        this.state = b.deepCopy();
        this.terminalState = b.isEndGame();
        wins = 0;
        plays = 0;
    }

    public Position getMove() {
        return move;
    }

    Set<Node> getChildren() {
        return children;
    }

    void logWin() {
        wins++;
        plays++;
    }

    void logLoss() {
        plays++;
    }

    Node getParent() {
        return parent;
    }

    Set<Position> getPossibleChildren() {
        return possibleChildren;
    }

    Board getState() {
        return state.deepCopy();
    }

    boolean isExpanded() {
        return possibleChildren.size() == 0;
    }

    boolean isTerminalState() {
        return terminalState;
    }

    Node addChild(Position position) {
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

    @Override
    public String toString() {
        return String.valueOf(getWinProbability());
    }

}

