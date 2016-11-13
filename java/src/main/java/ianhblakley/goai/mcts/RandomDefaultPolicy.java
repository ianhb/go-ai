package ianhblakley.goai.mcts;

import ianhblakley.goai.bots.RandomBot;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;

/**
 * Simulates a game with random bots
 *
 * Created by ian on 10/17/16.
 */
class RandomDefaultPolicy implements DefaultPolicy {

    private final Node leafNode;
    private final PositionState color;

    private RandomDefaultPolicy(Node leaf, PositionState color) {
        this.leafNode = leaf;
        this.color = color;
    }

    /**
     * Simulates a game using two {@link RandomBot} instances and returns the winning color
     * Simulation is started at the board state of the node passed in the constructor
     *
     * @return winning color
     */
    @Override
    public PositionState simulate() {
        RandomBot black = new RandomBot(PositionState.BLACK);
        RandomBot white = new RandomBot(PositionState.WHITE);
        Board currentBoard = leafNode.getState().deepCopy();
        Game simulation = new Game(currentBoard, black, white);
        simulation.play(false);
        return simulation.getWinner();
    }

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

    static class RandomDefaultPolicyFactory implements DefaultPolicyFactory {
        @Override
        public DefaultPolicy getDefaultPolicy(Node n, PositionState color) {
            return new RandomDefaultPolicy(n, color);
        }
    }
}
