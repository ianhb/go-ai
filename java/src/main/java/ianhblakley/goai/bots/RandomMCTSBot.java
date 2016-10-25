package ianhblakley.goai.bots;

import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;

/**
 * MCTS Bot that randomly selects children for expansion
 * <p>
 * Created by ian on 10/25/16.
 */
public class RandomMCTSBot extends MCTSBot {

    public RandomMCTSBot(PositionState color) {
        super(color);
        mcts = MCTS.randomMCTS(color);
    }
}
