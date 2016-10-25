package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.PositionState;

/**
 * Simulates a game from state down
 *
 * Created by ian on 10/17/16.
 */
interface DefaultPolicy {

    PositionState simulate(Node node);
}
