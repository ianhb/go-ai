package ianhblakley.goai.mcts;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.framework.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;


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
        this.color = Utils.getOppositeColor(color);
    }

    public static MCTS randomMCTS(PositionState color) {
        return new MCTS(new RandomTreePolicy(), new RandomDefaultPolicy.RandomDefaultPolicyFactory(), color);
    }

    public static MCTS uctMCTS(PositionState color) {
        return new MCTS(new UCTTreePolicy(), new RandomDefaultPolicy.RandomDefaultPolicyFactory(), color);
    }

    public Position getMove(Board board) {
        MonteCarloTree tree = new MonteCarloTree(board, color);
        assert Constants.THREAD_COUNT > 0;
        if (Constants.THREAD_COUNT < 2) {
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) < COMPUTE_THRESHOLD * 1000) {
                Node select = treePolicy.select(tree.getRoot());
                DefaultPolicy policy = defaultPolicyFactory.getDefaultPolicy(select, color);
                policy.run();
            }
        } else {
            ThreadPoolExecutor consumers = new ThreadPoolExecutor(Constants.THREAD_COUNT - 1, Constants.THREAD_COUNT - 1,
                    COMPUTE_THRESHOLD, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) < COMPUTE_THRESHOLD * 1000 && !consumers.isShutdown()) {
                Node select = treePolicy.select(tree.getRoot());
                DefaultPolicy policy = defaultPolicyFactory.getDefaultPolicy(select, color);
                consumers.submit(policy);
            }
            consumers.shutdownNow();
            logger.debug("Tree Size: %s", tree.getTreeSize());
            try {
                consumers.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Node bestMove = treePolicy.getBestMove(tree.getRoot(), 0);
        return bestMove.getMove();
    }

}
