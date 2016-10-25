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

    private final RandomBot black;
    private final RandomBot white;

    RandomDefaultPolicy() {
        black = new RandomBot(PositionState.BLACK);
        white = new RandomBot(PositionState.WHITE);
    }

    @Override
    public PositionState simulate(Node node) {
        Board currentBoard = node.getState();
        Game simulation = new Game(currentBoard, black, white);
        simulation.play(false);
        return simulation.getWinner();
    }
}
