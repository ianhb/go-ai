package ianhblakley.goai.bots;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.PositionState;

/**
 * Interface to encompass a bot
 * Uses a single method getPlay to get the move of the bot
 * <p>
 * Created by ian on 10/12/16.
 */
public interface Bot {

    Move getPlay(Board board, PositionState[][] oldBoard, int turnNumber);

    boolean checkCanPlay();

}
