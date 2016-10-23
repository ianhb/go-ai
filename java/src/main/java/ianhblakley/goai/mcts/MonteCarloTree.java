package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;

/**
 * Created by ian on 10/17/16.
 */
class MonteCarloTree {

    private State root;

    MonteCarloTree() {
        root = new State(null, new Board());
    }

    /**
     * Checks if the root of the tree represents the last move played by the bot
     * @param b current board
     * @return if the board of the root equals the previous board
     */
    boolean checkRootIsBoard(Board b) {
        return root.represents(b.getPreviousState());
    }

    State selectPlayedState(Board b) {
        for (State child : root.getChildren()) {
            if (child.represents(b)) {
                root = child;
                child.setRoot();
                return child;
            }
        }
        root = new State(null, b.deepCopy());
        return root;
    }

}
