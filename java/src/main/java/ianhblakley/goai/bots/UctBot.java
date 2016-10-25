package ianhblakley.goai.bots;

import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;

/**
 * Bot that uses UCT MCTS instead of pure MCTS
 * <p>
 * Created by ian on 10/17/16.
 */
public class UctBot extends MCTSBot {

    public UctBot(PositionState color) {
        super(color);
        mcts = MCTS.uctMCTS(color);
    }

}
