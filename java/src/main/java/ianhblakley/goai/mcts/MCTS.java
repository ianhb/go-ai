package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Framework to run MCTS
 * Uses a {@link TreePolicy} and {@link DefaultPolicy}
 *
 * Created by ian on 10/17/16.
 */
public class MCTS {

    private static final Logger logger = LogManager.getFormatterLogger(MCTS.class);
    private static final int COMPUTE_THRESHOLD = 30;

    private final TreePolicy treePolicy;
    private final DefaultPolicy.DefaultPolicyFactory defaultPolicyFactory;
    private final PositionState color;

    private MCTS(TreePolicy treePolicy, DefaultPolicy.DefaultPolicyFactory defaultPolicyFactory, PositionState color) {
        this.treePolicy = treePolicy;
        this.defaultPolicyFactory = defaultPolicyFactory;
        this.color = color;
    }

    public static MCTS randomMCTS(PositionState color) {
        return new MCTS(new RandomTreePolicy(), new RandomDefaultPolicy.RandomDefaultPolicyFactory(), color);
    }

    public static MCTS uctMCTS(PositionState color) {
        return new MCTS(new UCTTreePolicy(), new RandomDefaultPolicy.RandomDefaultPolicyFactory(), color);
    }

    public Position getMove(Board board) {
        MonteCarloTree tree = new MonteCarloTree(board, color);
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < COMPUTE_THRESHOLD * 1000) {
            Node select = treePolicy.select(tree.getRoot());
            PositionState winner = defaultPolicyFactory.getDefaultPolicy(select, color).simulate();
            tree.backTrace(select, winner == color);
        }
        logger.debug("Tree Size: %s", tree.getTreeSize());
        Node bestMove = treePolicy.getBestMove(tree.getRoot(), 0);
        return bestMove.getMove();
    }

}
