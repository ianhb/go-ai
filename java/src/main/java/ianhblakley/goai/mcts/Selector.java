package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;

/**
 * Interface to select nodes for MCTS
 *
 * Created by ian on 10/17/16.
 */
interface Selector {

    /**
     * Selects a State from a given tree, board state and player color
     * @param tree tree to select from
     * @param board current board state
     * @return best state available
     */
    State select(MonteCarloTree tree, Board board);
}
