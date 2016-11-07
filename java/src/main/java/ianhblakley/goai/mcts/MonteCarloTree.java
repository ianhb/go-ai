package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.PositionState;

import java.util.*;

/**
 * Tree structure to hold the MCT for MCTS
 *
 * Created by ian on 10/17/16.
 */
class MonteCarloTree {

    private final Node root;

    MonteCarloTree(Board board, PositionState color) {
        root = new Node(null, board, null, color);
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

    /**
     * Creates a list of levels of the tree
     * Each sublist represents a layer of nodes in the tree
     * @return list of layers of nodes
     */
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

    /**
     * Returns the total number of nodes in the tree
     * @return # of nodes in tree
     */
    int getTreeSize() {
        int i = 0;
        for (List<Node> level : traverseLevels()) {
            i += level.size();
        }
        return i;
    }
}
