package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;

import java.util.Random;

/**
 * Selects a node at random
 *
 * Created by ian on 10/17/16.
 */
public class RandomSelector implements Selector {

    private Random random;

    public RandomSelector() {
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public State select(MonteCarloTree tree, Board board) {
        State currentState = tree.selectPlayedState(board);
        if (currentState.getChildren().size() == 0) {
            return currentState;
        }
        while (!currentState.isTerminalState()) {
            if (currentState.getChildren().size() == 0) {
                return currentState;
            }
            currentState = currentState.getChildren().get(random.nextInt(currentState.getChildren().size()));
        }
        return currentState;
    }
}
