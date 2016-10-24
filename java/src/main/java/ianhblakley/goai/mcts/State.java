package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;

import java.util.ArrayList;

/**
 * Holds the state of a game in the MCT
 * Represents a node in a tree
 *
 * Created by ian on 10/17/16.
 */
class State {

    private final Board board;
    private State parent;
    private ArrayList<State> children;
    private int wins;
    private int plays;
    private boolean terminalState;

    State(State parent, Board b) {
        this.parent = parent;
        board = b;
        wins = 0;
        plays = 0;
        children = new ArrayList<>();
        terminalState = board.isEndGame();
    }

    /**
     * Returns true if {@link #board} is equal to b
     * @param b board to check against
     * @return equality of boards
     */
    boolean represents(Board b) {
        return b.equals(board);
    }

    ArrayList<State> getChildren() {
        return children;
    }

    boolean isTerminalState() {
        return terminalState;
    }

    Board getBoard() {
        return board;
    }

    void logWin() {
        wins++;
        plays++;
    }

    void logLoss() {
        plays++;
    }

    State getParent() {
        return parent;
    }

    void setRoot() {
        parent = null;
    }

    void addChild(State s) {
        children.add(s);
    }

    double getWinProbability() {
        return (double) wins / (double) plays;
    }

    @Override
    public String toString() {
        return String.valueOf(getWinProbability());
    }
}

