package ianhblakley.goai.bots;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;

/**
 * Abstract bot that uses MCTS to select a move
 * Subclasses must use one of the static methods in {@link MCTS} to
 * instantiate {@link #mcts}
 * <p>
 * Created by ian on 10/17/16.
 */
abstract class MCTSBot extends AbstractBot {

    MCTS mcts;

    MCTSBot(PositionState color) {
        super(color);
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        assert mcts != null;
        if (checkCannotPlay()) {
            return new Move();
        }
        Position m = mcts.getMove(board);
        if (m == null) {
            return new Move();
        }
        playStone();
        return new Move(m, color);
    }
}
