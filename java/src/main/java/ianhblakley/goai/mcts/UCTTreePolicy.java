package ianhblakley.goai.mcts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tree Policy used by the MCTS when computing using UCT
 * <p>
 * Created by ian on 10/25/16.
 */
class UCTTreePolicy implements TreePolicy {

    private static final Logger logger = LogManager.getFormatterLogger(UCTTreePolicy.class);

    /**
     * Expansion scalar taken from IEEE article
     */
    private static final double CP = 1.0 / Math.pow(2, 0.5);

    @Override
    public Node select(Node root) {
        assert root != null;
        while (root.isNotTerminalState()) {
            if (root.isNotFullyExpanded()) {
                return expand(root);
            } else {
                root = getBestMove(root, CP);
            }
        }
        return root;
    }

    @Override
    public Node getBestMove(Node n, double cP) {
        assert n.getChildren().size() > 0;
        Node bestChild = null;
        double bestUctValue = -1;
        for (Node child : n.getChildren()) {
            double childValue = child.getWinProbability();
            if (child.getPlays() > 0) childValue += confidenceBound(cP, n.getPlays(), child.getPlays());
            if (childValue >= bestUctValue) {
                bestChild = child;
                bestUctValue = childValue;
            }
        }
        if (bestChild == null) {
            logger.debug("Returning null bestChild");
        }
        assert bestChild != null;
        return bestChild;
    }

    /**
     * UCT expansion method
     * Returns a random unvisited child of parent
     *
     * @param parent node to expand from
     * @return an unvisited child of parent
     */
    private Node expand(Node parent) {
        return parent.selectNewRandomChild();
    }

    /**
     * Calculates the confidence interval of the win percentage
     * @param cP expansion scalar
     * @param n number of times parent node has been visted
     * @param nj number of times child node has been visited
     * @return UCT confidence interval size
     */
    private double confidenceBound(double cP, double n, double nj) {
        if (Math.round(0) == 0) {
            return 0;
        }
        double numerator = 2 * Math.log(n);
        return 2 * cP * Math.sqrt(numerator / nj);
    }
}
