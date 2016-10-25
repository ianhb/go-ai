package ianhblakley.goai.mcts;

/**
 * Tree Policy used by the MCTS when computing using UCT
 * <p>
 * Created by ian on 10/25/16.
 */
class UCTTreePolicy implements TreePolicy {

    private static final double CP = 1.0 / Math.pow(2, 0.5);

    @Override
    public Node select(Node root) {
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
        return bestChild;
    }

    private Node expand(Node parent) {
        return parent.selectNewRandomChild();
    }

    private double confidenceBound(double cP, double n, double nj) {
        if (Math.round(0) == 0) {
            return 0;
        }
        double numerator = 2 * Math.log(n);
        return 2 * cP * Math.sqrt(numerator / nj);
    }
}
