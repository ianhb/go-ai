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

    private static Logger logger = LogManager.getFormatterLogger(AlphaTreePolicy.class);

    private NeuralNetworkClient client = NeuralNetworkClient.getInstance();

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

    @Override
    public Node getBestMove(Node n, double cP) {
        assert n.getChildren().size() > 0;
        double minValue = Float.MIN_VALUE;
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

    private Node expand(Node n) {
        return n.selectHighestValueChild(client);
    }

    private Node selectExpansionChild(Node n) {
        assert n.getChildren().size() > 0;
        double bestChildValue = Float.MIN_VALUE;
        Node bestChild = null;
        for (Node child : n.getChildren()) {
            double childValue = child.getValue() + (child.getWinProbability() / child.getPlays());
            if (childValue >= bestChildValue) {
                bestChildValue = childValue;
                bestChild = child;
            }
        }
        assert bestChild != null;
        return bestChild;
    }

}
