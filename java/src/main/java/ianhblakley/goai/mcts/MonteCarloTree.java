package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;
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

    private State root;

    MonteCarloTree() {
        root = new State(null, new Board());
    }

    /**
     * Checks if the root of the tree represents the last move played by the bot
     * @param b current board
     * @return if the board of the root equals the previous board
     */
    boolean checkRootIsBoard(Board b) {
        return root.represents(b.getPreviousState());
    }

    State selectPlayedState(Board b) {
        logger.debug("Current State: %s", b.toString());
        for (State child : root.getChildren()) {
            logger.debug("Child State: %s", child.getBoard().toString());
            if (child.represents(b)) {
                root = child;
                child.setRoot();
                return child;
            }
        }
        root = new State(null, b.deepCopy());
        logger.debug("Opponent Move Not Found");
        return root;
    }

    State selectBestChild() {
        logger.debug("Children: %s", root.getChildren().size());
        double winProb = 0;
        State state = null;
        for (State child : root.getChildren()) {
            if (child.getWinProbability() > winProb) {
                winProb = child.getWinProbability();
                state = child;
            }
        }
        return state;
    }

    @Override
    public String toString() {
        List<List<State>> levels = traverseLevels();
        StringBuilder builder = new StringBuilder();
        for (List<State> level : levels) {
            for (State state : level) {
                builder.append(state.toString()).append(" ");
            }
            builder.append('\n');
        }
        return "MonteCarloTree{" + "\n" + builder.toString() + "}";
    }

    private List<List<State>> traverseLevels() {
        if (root == null) {
            return Collections.emptyList();
        }
        List<List<State>> levels = new LinkedList<>();
        Queue<State> states = new LinkedList<>();
        states.add(root);
        while (!states.isEmpty()) {
            List<State> level = new ArrayList<>(states.size());
            levels.add(level);
            for (State s : new ArrayList<>(states)) {
                level.add(s);
                states.addAll(s.getChildren());
                states.poll();
            }
        }
        return levels;
    }
}
