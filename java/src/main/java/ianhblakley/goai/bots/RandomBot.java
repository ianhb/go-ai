package ianhblakley.goai.bots;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;

/**
 * Random Bot that selects a random open space to play on
 * <p>
 * Created by ian on 10/12/16.
 */
public class RandomBot implements Bot {

    private final static Logger logger = LogManager.getFormatterLogger(RandomBot.class);

    private final PositionState color;

    public RandomBot(PositionState color) {
        assert color != null;
        assert !color.equals(PositionState.EMPTY);
        this.color = color;
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        Set<Position> positions = board.getAvailableSpaces();
        if (positions.size() == 0) {
            return new Move();
        }
        int item = new Random().nextInt(positions.size());
        int i = 0;
        logger.info("Getting the %sth element", item);
        for (Position p : positions) {
            if (i == item) {
                logger.info("Player %s played at %s on turn %s", color.toString(), p, turnNumber);
                return new Move(p, color, turnNumber);
            } else {
                i++;
            }
        }
        return null;
    }
}
