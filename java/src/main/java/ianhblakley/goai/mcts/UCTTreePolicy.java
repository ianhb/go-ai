package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Position;

/**
 * Tree Policy used by the MCTS when computing using UCT
 * <p>
 * Created by ian on 10/25/16.
 */
class UCTTreePolicy implements TreePolicy {
    @Override
    public Node select(MonteCarloTree tree) {
        return null;
    }

    @Override
    public Position getBestMove(Node n, double cP) {
        return null;
    }
}
