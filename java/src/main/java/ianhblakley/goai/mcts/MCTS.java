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

    private final TreePolicy treePolicy;
    private final DefaultPolicy defaultPolicy;
    private final PositionState color;
    private MonteCarloTree tree;

    private MCTS(TreePolicy treePolicy, DefaultPolicy defaultPolicy, PositionState color) {
        this.treePolicy = treePolicy;
        this.defaultPolicy = defaultPolicy;
        this.color = color;
    }

    public static MCTS randomMCTS(PositionState color) {
        return new MCTS(new RandomTreePolicy(), new RandomDefaultPolicy(), color);
    }

    public Position getMove(Board board) {
        tree = new MonteCarloTree(board, color);
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < 1000) {
            Node select = treePolicy.select(tree);
            PositionState winner = defaultPolicy.simulate(select);
            tree.backTrace(select, winner == color);
        }
        logger.debug("Tree Size: %s", tree.getTreeSize());
        return getBestMove();
    }

    private Position getBestMove() {
        return tree.getBestMove();
    }




}
