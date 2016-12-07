package ianhblakley.goai.bots;

import ianhblakley.goai.framework.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Random Bot that selects a random open space to play on
 * <p>
 * Created by ian on 10/12/16.
 */
public class RandomBot extends AbstractBot {

    private static final Logger logger = LogManager.getFormatterLogger(RandomBot.class);

    public RandomBot(PositionState color) {
        super(color);
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        if (checkCannotPlay()) return new Move(color);
        Set<Position> positions = board.getAvailableSpaces();
        Set<Position> legalMoves = new HashSet<>();
        for (Position p : positions) {
            Move m = new Move(p, color);
            if (StateChecker.isLegalMove(m, board)) {
                legalMoves.add(p);
            }
        }
        if (legalMoves.size() == 0) {
            return new Move(color);
        }
        List<Position> randomPositions = new ArrayList<>(legalMoves);
        Collections.shuffle(randomPositions);
        Move m = new Move(randomPositions.get(0), color);
        assert (StateChecker.isLegalMove(m, board));
        if (resign(board, m.getPosition(), turnNumber)) {
            return new Move(color);
        }
        playStone();
        logger.trace("Playing %s", m);
        return m;
    }

    @Override
    public String toString() {
        return "Random Bot";
    }

}
