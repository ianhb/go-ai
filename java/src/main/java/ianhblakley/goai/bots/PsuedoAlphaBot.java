package ianhblakley.goai.bots;

import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;

/**
 * MCTS which use {@link ianhblakley.goai.mcts.AlphaTreePolicy} and {@link ianhblakley.goai.mcts.RandomDefaultPolicy}
 * <p>
 * Created by ian on 12/3/16.
 */
public class PsuedoAlphaBot extends MCTSBot {

    PsuedoAlphaBot(PositionState color) {
        super(color);
        mcts = MCTS.psuedoMCTS(color);
    }

    @Override
    public String toString() {
        return "Psuedo Alpha Bot";
    }
}
