package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.PositionState;

/**
 * Simulates a game from state down
 *
 * Created by ian on 10/17/16.
 */
interface DefaultPolicy extends Runnable {

    /**
     * Simulates a game based on provided node and returns the winner of the simulation.
     * The game is stated at the state of the node passed in the constructor
     *
     * @return winning color
     */
    PositionState simulate();

    interface DefaultPolicyFactory {
        DefaultPolicy getDefaultPolicy(Node n, PositionState color);
    }
}
