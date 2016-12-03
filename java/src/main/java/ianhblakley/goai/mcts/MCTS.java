package ianhblakley.goai.mcts;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.framework.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ianhblakley.goai.Constants.COMPUTE_THRESHOLD;


/**
 * Framework to run MCTS
 * Uses a {@link TreePolicy} and {@link DefaultPolicy}
 *
 * Created by ian on 10/17/16.
 */
public class MCTS {

    private static final Logger logger = LogManager.getFormatterLogger(MCTS.class);

    private final TreePolicy treePolicy;
    private final DefaultPolicy.DefaultPolicyFactory defaultPolicyFactory;
    private final PositionState color;

    private MCTS(TreePolicy treePolicy, DefaultPolicy.DefaultPolicyFactory defaultPolicyFactory, PositionState color) {
        this.treePolicy = treePolicy;
        this.defaultPolicyFactory = defaultPolicyFactory;
        this.color = Utils.getOppositeColor(color);
    }

    /**
     * Creates a top level MCTS for the color
     * Uses {@link RandomTreePolicy} and {@link RandomDefaultPolicy}
     *
     * @param color bot's color
     * @return reusable MCTS for finding best moves
     */
    public static MCTS randomMCTS(PositionState color) {
        return new MCTS(new RandomTreePolicy(), new RandomDefaultPolicy.RandomDefaultPolicyFactory(), color);
    }

    /**
     * Creates a top level MCTS for the color
     * Uses {@link UCTTreePolicy} and {@link RandomDefaultPolicy}
     *
     * @param color bot's color
     * @return reusable MCTS for finding best moves
     */
    public static MCTS uctMCTS(PositionState color) {
        return new MCTS(new UCTTreePolicy(), new RandomDefaultPolicy.RandomDefaultPolicyFactory(), color);
    }

    public static MCTS alphaMCTS(PositionState color) {
        return new MCTS(new AlphaTreePolicy(), new AlphaDefaultPolicy.AlphaDefaultPolicyFactory(), color);
    }

    /**
     * Finds the best move for the given board for the player color
     *
     * @param board current board state
     * @return best position to play according to MCTS
     */
    @SuppressWarnings("ConstantConditions")
    public Position getMove(Board board) {
        MonteCarloTree tree = new MonteCarloTree(board, color);
        assert Constants.THREAD_COUNT > 0;
        // If only one thread, perform selection and then simulation sequentially
        // Else use a producer, consumer queue where one thread expands the tree and THREAD_COUNT - 1 threads
        // Simulate and backpropogate
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
            logger.debug("Tree Size: %s Games Played: %s", tree.getTreeSize(), tree.getRoot().getPlays());
            try {
                consumers.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Node bestMove = treePolicy.getBestMove(tree.getRoot(), 0);
        if (bestMove == null) {
            return null;
        }
        return bestMove.getMove();
    }

}
