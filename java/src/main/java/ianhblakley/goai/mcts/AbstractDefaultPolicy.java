package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.PositionState;

/**
 * Abstract Class to handle the back-tracing of game results up the tree
 * <p>
 * Created by ian on 11/24/16.
 */
abstract class AbstractDefaultPolicy implements DefaultPolicy {

    final Node leafNode;
    private final PositionState color;

    AbstractDefaultPolicy(Node leafNode, PositionState color) {
        this.leafNode = leafNode;
        this.color = color;
    }

    @Override
    public abstract PositionState simulate();

    /**
     * Runs a simulation of a game and backtraces the result up the tree
     */
    @Override
    public void run() {
        PositionState winner = simulate();
        Node curNode = leafNode;
        boolean won = winner == color;
        while (curNode != null) {
            if (won) curNode.logWin();
            else curNode.logLoss();
            won = !won;
            curNode = curNode.getParent();
        }
    }

}
