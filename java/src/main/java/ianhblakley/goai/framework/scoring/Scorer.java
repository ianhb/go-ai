package ianhblakley.goai.framework.scoring;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.PositionState;

/**
 * Interface to handle the different types of scoring
 *
 * Created by ian on 10/14/16.
 */
public abstract class Scorer {

    public static Scorer getDefaultScorer() {
        return new AreaScorer();
    }

    int whiteScore;
    int blackScore;

    abstract void score(Board board);

    public PositionState winner(Board board) {
        score(board);
        return blackScore > whiteScore ? PositionState.BLACK : PositionState.WHITE;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }
}
