package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Tree structure to hold the MCT for MCTS
 *
 * Created by ian on 10/17/16.
 */
class MonteCarloTree {

    private static final Logger logger = LogManager.getFormatterLogger(MonteCarloTree.class);

    private final Node root;
    private final PositionState color;

    MonteCarloTree(Board board, PositionState color) {
        root = new Node(null, board.deepCopy(), null, color);
        this.color = color;
    }

    void backTrace(Node expansionNode, boolean won) {
        while (expansionNode != null) {
            if (won) { expansionNode.logWin(); }
            else { expansionNode.logLoss(); }
            expansionNode = expansionNode.getParent();
        }
    }

    Position getBestMove() {
        double bestProb = 0;
        Position bestMove = null;
        for (Node n : root.getChildren()) {
            if (n.getWinProbability() >= bestProb) {
                bestProb = n.getWinProbability();
                bestMove = n.getMove();
            }
        }
        return bestMove;
    }

    Node getRoot() {
        return root;
    }

    @Override
    public String toString() {
        List<List<Node>> levels = traverseLevels();
        StringBuilder builder = new StringBuilder();
        for (List<Node> level : levels) {
            for (Node node : level) {
                builder.append(node.toString()).append(" ");
            }
            builder.append('\n');
        }
        return "MonteCarloTree{" + "\n" + builder.toString() + "}";
    }

    private List<List<Node>> traverseLevels() {
        if (root == null) {
            return Collections.emptyList();
        }
        List<List<Node>> levels = new LinkedList<>();
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);
        while (!nodes.isEmpty()) {
            List<Node> level = new ArrayList<>(nodes.size());
            levels.add(level);
            for (Node s : new ArrayList<>(nodes)) {
                level.add(s);
                nodes.addAll(s.getChildren());
                nodes.poll();
            }
        }
        return levels;
    }

    int getTreeSize() {
        int i = 0;
        for (List<Node> level : traverseLevels()) {
            i += level.size();
        }
        return i;
    }
}
