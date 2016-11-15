package ianhblakley.goai.bots;

import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;

/**
 * Bot that uses UCT MCTS instead of pure MCTS
 * <p>
 * Created by ian on 10/17/16.
 */
class UctBot extends MCTSBot {

    UctBot(PositionState color) {
        super(color);
        mcts = MCTS.uctMCTS(color);
    }

    @Override
    public String toString() {
        return "UCT MCTS Bot";
    }

}
