package ianhblakley.goai.bots;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.PositionState;

/**
 * Bot that uses UCT MCTS instead of pure MCTS
 * <p>
 * Created by ian on 10/17/16.
 */
public class UctBot extends AbstractBot {

    public UctBot(PositionState color) {
        super(color);
    }

    @Override
    public Move getPlay(Board board, PositionState[][] oldBoard, int turnNumber) {
        return null;
    }

}
