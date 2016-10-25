package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;

/**
 * Interface to select nodes for MCTS
 *
 * Created by ian on 10/17/16.
 */
interface TreePolicy {

    /**
     * Selects a Node from a given tree, board state and player color
     * @param tree tree to select from
     * @return best state available
     */
    Node select(MonteCarloTree tree);
}
