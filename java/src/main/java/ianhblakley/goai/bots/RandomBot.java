package ianhblakley.goai.bots;

import ianhblakley.goai.framework.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Random Bot that selects a random open space to play on
 * <p>
 * Created by ian on 10/12/16.
 */
public class RandomBot extends AbstractBot {


    public RandomBot(PositionState color) {
        super(color);
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        if (checkCannotPlay()) return new Move();
        Set<Position> positions = board.getAvailableSpaces();
        if (positions.size() == 0) {
            return new Move();
        }
        List<Position> randomPositions = new ArrayList<>(positions);
        Collections.shuffle(randomPositions);
        for (Position p : randomPositions) {
            Move m = new Move(p, color);
            if (StateChecker.isLegalMove(m, board)) {
                playStone();
                return m;
            }
        }
        return new Move();
    }
}
