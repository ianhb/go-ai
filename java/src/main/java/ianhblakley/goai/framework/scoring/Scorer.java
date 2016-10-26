package ianhblakley.goai.framework.scoring;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.PositionState;

/**
 * Interface to handle the different types of scoring
 *
 * Created by ian on 10/14/16.
 */
public abstract class Scorer {

    int whiteScore;
    int blackScore;

    /**
     * Returns an instance of {@link AreaScorer}
     */
    public static Scorer getDefaultScorer() {
        return new AreaScorer();
    }

    /**
     * Scores the board and sets the winner
     * Instantiations must set whiteScore and blackScore
     *
     * @param board   board to score
     * @param verbose whether or not to log
     */
    abstract void score(Board board, boolean verbose);

    /**
     * Scores a game and returns the winner
     * @param board board to score
     * @param verbose whether to display logs
     * @return color of winning color
     */
    public PositionState winner(Board board, boolean verbose) {
        score(board, verbose);
        return blackScore > whiteScore ? PositionState.BLACK : PositionState.WHITE;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }
}
