package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Tree Policy used by the MCTS when randomly selecting children to expand
 *
 * Created by ian on 10/17/16.
 */
class RandomTreePolicy implements TreePolicy {

    private static final Logger logger = LogManager.getFormatterLogger(RandomTreePolicy.class);

    private final Random random;

    RandomTreePolicy() {
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public Node select(MonteCarloTree tree) {
        Queue<Node> searchQueue = new LinkedList<>();
        searchQueue.add(tree.getRoot());
        Node deque = null;
        while (searchQueue.size() > 0) {
            deque = searchQueue.poll();
            if (!deque.isTerminalState() && !deque.isExpanded()) {
                Set<Position> possibleMoves = deque.getPossibleChildren();
                Position randomMove = randomSelect(possibleMoves);
                return deque.addChild(randomMove);
            }
            searchQueue.addAll(deque.getChildren());
        }
        logger.debug("Select returning %s Node", deque);
        return deque;
    }

    private Position randomSelect(Set<Position> moves) {
        int size = moves.size();
        int randomInt = random.nextInt(size);
        int i = 0;
        for (Position p : moves) {
            if (i == randomInt) {
                return p;
            }
            i++;
        }
        logger.debug("Random returning null Node");
        return null;
    }

    @Override
    public Position getBestMove(Node n, double cP) {
        double bestProb = 0;
        Position bestMove = null;
        for (Node child : n.getChildren()) {
            if (child.getWinProbability() >= bestProb) {
                bestProb = child.getWinProbability();
                bestMove = child.getMove();
            }
        }
        return bestMove;
    }
}
