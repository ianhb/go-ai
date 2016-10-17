package ianhblakley.goai.bots;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract Bot that handles ensuring that no bot has played more stones than supposed to
 * <p>
 * Created by ian on 10/17/16.
 */
abstract class AbstractBot implements Bot {

    private final static Logger logger = LogManager.getFormatterLogger(RandomBot.class);

    private int stones;

    AbstractBot() {
    }

    AbstractBot(PositionState color, int boardSize) {
        stones = (int) (Math.floor(Math.pow(boardSize, 2)) / 2);
        if (color.equals(PositionState.BLACK)) stones++;
    }

    /**
     * Returns the move the bot deems is best given the current board
     * Must call {@link #playStone()} if returns a non-null move and can't call {@link #playStone()} if returns null
     * Must check {@link #checkCanPlay()} before returning a non-null Move
     *
     * @param board      the current board
     * @param oldBoard   the board one play ago
     * @param turnNumber the turn number
     * @return the next move to play or null if can't/shouldn't play a move
     */
    @Override
    public abstract Move getPlay(Board board, PositionState[][] oldBoard, int turnNumber);

    void playStone() {
        stones--;
    }

    @Override
    public boolean checkCanPlay() {
        return stones > 0;
    }
}
