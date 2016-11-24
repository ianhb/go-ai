package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.PositionState;

/**
 * Abstract Class to handle the back-tracing of game results up the tree
 * <p>
 * Created by ian on 11/24/16.
 */
abstract class AbstractDefaultPolicy implements DefaultPolicy {


    @Override
    public abstract PositionState simulate();


}
