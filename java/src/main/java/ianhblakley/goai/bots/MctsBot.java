package ianhblakley.goai.bots;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;

/**
 * Bot that uses a pure MCTS to decide the moves to play
 * <p>
 * <p>
 * Created by ian on 10/17/16.
 */
public class MctsBot extends AbstractBot {

    private final MCTS mcts;

    public MctsBot(PositionState color) {
        super(color);
        mcts = MCTS.randomMCTS(color);
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        if (!checkCanPlay()) { return new Move(); }
        Position m = mcts.getMove(board);
        if (m == null) {
            return new Move();
        }
        playStone();
        return new Move(m, color, turnNumber);
    }
}
