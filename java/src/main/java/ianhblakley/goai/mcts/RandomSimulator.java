package ianhblakley.goai.mcts;

import ianhblakley.goai.bots.RandomBot;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;

/**
 * Simulates a game with random bots
 *
 * Created by ian on 10/17/16.
 */
public class RandomSimulator implements Simulator {
    @Override
    public PositionState simulate(MonteCarloTree tree, State state) {
        Game randomGame = new Game(state.getBoard(), new RandomBot(PositionState.BLACK),
                new RandomBot(PositionState.WHITE));
        randomGame.play(false);
        return randomGame.getWinner();
    }
}
