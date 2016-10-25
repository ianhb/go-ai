package ianhblakley.goai.mcts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Tree Policy used by the MCTS when randomly selecting children to expand
 *
 * Created by ian on 10/17/16.
 */
class RandomTreePolicy implements TreePolicy {

    private static final Logger logger = LogManager.getFormatterLogger(RandomTreePolicy.class);

    @Override
    public Node select(Node root) {
        Queue<Node> searchQueue = new LinkedList<>();
        searchQueue.add(root);
        Node deque = null;
        while (searchQueue.size() > 0) {
            deque = searchQueue.poll();
            if (deque.isNotTerminalState() && deque.isNotFullyExpanded()) {
                return deque.selectNewRandomChild();
            }
            searchQueue.addAll(deque.getChildren());
        }
        logger.debug("Select returning %s Node", deque);
        return deque;
    }


    @Override
    public Node getBestMove(Node n, double cP) {
        double bestProb = -1;
        Node bestMove = null;
        for (Node child : n.getChildren()) {
            if (child.getWinProbability() >= bestProb) {
                bestProb = child.getWinProbability();
                bestMove = child;
            }
        }
        return bestMove;
    }
}
