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
class RandomDefaultPolicy extends AbstractDefaultPolicy {

    private RandomDefaultPolicy(Node leaf, PositionState color) {
        super(leaf, color);
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
        currentBoard.verifyIntegrity();
        Game simulation = new Game(currentBoard, black, white);
        simulation.play(false);
        return simulation.getWinner();
    }


    static class RandomDefaultPolicyFactory implements DefaultPolicyFactory {
        @Override
        public DefaultPolicy getDefaultPolicy(Node n, PositionState color) {
            return new RandomDefaultPolicy(n, color);
        }
    }
}
