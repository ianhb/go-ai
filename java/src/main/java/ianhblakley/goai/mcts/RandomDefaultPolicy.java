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

    /**
     * Simulates a game using two {@link RandomBot} instances and returns the winning color
     * Simulation is started at the board state of node
     *
     * @param node start node
     * @return winning color
     */
    @Override
    public PositionState simulate(Node node) {
        RandomBot black = new RandomBot(PositionState.BLACK);
        RandomBot white = new RandomBot(PositionState.WHITE);
        Board currentBoard = node.getState();
        Game simulation = new Game(currentBoard, black, white);
        simulation.play(false);
        return simulation.getWinner();
    }
}
