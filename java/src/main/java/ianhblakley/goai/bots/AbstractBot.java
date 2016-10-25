package ianhblakley.goai.bots;

import ianhblakley.goai.Constants;
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
    protected final PositionState color;
    private int stones;

    AbstractBot(PositionState color) {
        assert color != null;
        assert color != PositionState.EMPTY;
        this.color = color;
        stones = (int) (Math.floor(Math.pow(Constants.BOARDSIZE, 2)) / 2);
        if (color.equals(PositionState.BLACK)) stones++;
    }

    /**
     * Returns the move the bot deems is best given the current board
     * Must call {@link #playStone()} if returns a non-null move and can't call {@link #playStone()} if returns null
     * Must check {@link #checkCanPlay()} before returning a non-null Move
     *
     * @param board      the current board
     * @param turnNumber the turn number
     * @return the next move to play or null if can't/shouldn't play a move
     */
    @Override
    public abstract Move getPlay(Board board, int turnNumber);

    void playStone() {
        stones--;
    }

    /**
     * Returns if the bot has enough stones to play a move
     *
     * @return whether this has enought stones to play
     */
    @Override
    public boolean checkCanPlay() {
        return stones > 0;
    }
}
