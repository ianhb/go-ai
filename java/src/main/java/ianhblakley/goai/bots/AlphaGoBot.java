package ianhblakley.goai.bots;

import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;

/**
 * Bot that uses the Google Alpha-Go strategy of combining neural networks and MCTS to select a move
 * <p>
 * Created by ian on 11/24/16.
 */
public class AlphaGoBot extends MCTSBot {

    AlphaGoBot(PositionState color) {
        super(color);
        mcts = MCTS.alphaMCTS(color);
    }

    @Override
    public String toString() {
        return "Alpha Go Bot";
    }
}
