package ianhblakley.goai.mcts;

/**
 * Created by ian on 10/17/16.
 */
public interface Expander {

    State expand(MonteCarloTree tree, State selectedState);
}
