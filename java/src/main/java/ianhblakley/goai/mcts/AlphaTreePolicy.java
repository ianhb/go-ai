package ianhblakley.goai.mcts;

import ianhblakley.goai.neuralnetworkconnection.NeuralNetworkClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tree Policy that expands using the Alpha-Go strategy of balancing win percentage and expansion
 * <p>
 * Created by ian on 11/24/16.
 */
class AlphaTreePolicy implements TreePolicy {

    private static final Logger logger = LogManager.getFormatterLogger(AlphaTreePolicy.class);

    private final NeuralNetworkClient client = NeuralNetworkClient.getInstance();

    @Override
    public Node select(Node root) {
        assert root != null;
        while (root.isNotTerminalState()) {
            if (root.isNotFullyExpanded()) {
                return expand(root);
            } else {
                root = selectExpansionChild(root);
            }
        }
        return root;
    }

    /**
     * Best move is decided by a sum of Neural Net Value and win probability
     *
     * @param n  parent node
     * @param cP expansion factor for UCT
     * @return best immediate child of n
     */
    @Override
    public Node getBestMove(Node n, double cP) {
        assert n.getChildren().size() > 0;
        double minValue = Float.NEGATIVE_INFINITY;
        Node bestChild = null;
        for (Node child : n.getChildren()) {
            double childValue = child.getValue() + child.getWinProbability();
            if (childValue >= minValue) {
                minValue = childValue;
                bestChild = child;
            }
        }
        assert bestChild != null;
        logger.trace("Returning child %s", bestChild);
        return bestChild;
    }

    /**
     * Expands by selecting the child state that has the highest neural net value
     * @param n node to expand
     * @return child of n with highest value
     */
    private Node expand(Node n) {
        return n.selectHighestValueChild(client);
    }

    /**
     * Expands based on the child with the highest combined value and win probability
     * Divides win probability to encourage expansion
     * @param n node to select child from
     * @return best child to expand
     */
    private Node selectExpansionChild(Node n) {
        assert n.getChildren().size() > 0;
        double bestChildValue = Float.NEGATIVE_INFINITY;
        Node bestChild = null;
        for (Node child : n.getChildren()) {
            double childValue = child.getValue();
            if (child.getPlays() > 0) childValue += (child.getWinProbability() / child.getPlays() + 1);
            if (childValue >= bestChildValue) {
                bestChildValue = childValue;
                bestChild = child;
            }
        }
        assert bestChild != null;
        return bestChild;
    }

}
