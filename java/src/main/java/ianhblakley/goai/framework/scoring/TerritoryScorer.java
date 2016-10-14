package ianhblakley.goai.framework.scoring;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.PositionState;

/**
 * Scores the board in the standard Territory scoring method
 * Japanese and Korean Rules
 *
 * Created by ian on 10/14/16.
 */
class TerritoryScorer extends Scorer {

    @Override
    void score(Board board) {
        blackScore = board.getWhiteCaptured();
        whiteScore = board.getBlackCaptured();
    }

    private int getArea(Board board, PositionState color) {
        return 0;
    }
}
