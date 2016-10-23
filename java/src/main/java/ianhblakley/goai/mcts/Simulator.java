package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.PositionState;

/**
 * Created by ian on 10/17/16.
 */
public interface Simulator {

    PositionState simulate(MonteCarloTree tree, State state);
}
