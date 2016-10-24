package ianhblakley.goai.bots;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.mcts.MCTS;
import ianhblakley.goai.mcts.RandomExpander;
import ianhblakley.goai.mcts.RandomSelector;
import ianhblakley.goai.mcts.RandomSimulator;

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
        mcts = new MCTS(new RandomSelector(), new RandomExpander(), new RandomSimulator());
    }

    @Override
    public Move getPlay(Board board, PositionState[][] oldBoard, int turnNumber) {
        return mcts.getMove(board, color, turnNumber);
    }
}
