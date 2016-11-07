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
     * The game is stated at the state of node
     *
     * @param node start node
     * @return winning color
     */
    PositionState simulate(Node node);

    interface DefaultPolicyFactory {
        DefaultPolicy getDefaultFactory(Node n, PositionState color);
    }
}
