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

    private final PositionState color;

    public RandomBot(PositionState color, int boardSize) {
        super(color, boardSize);
        assert color != null;
        assert !color.equals(PositionState.EMPTY);
        this.color = color;
    }

    @Override
    public Move getPlay(Board board, PositionState[][] oldBoard, int turnNumber) {
        if (!checkCanPlay()) return new Move();
        Set<Position> positions = board.getAvailableSpaces();
        if (positions.size() == 0) {
            return new Move();
        }
        List<Position> randomPositions = new ArrayList<>(positions);
        Collections.shuffle(randomPositions);
        for (Position p : randomPositions) {
            Move m = new Move(p, color, turnNumber);
            if (!(StateChecker.checkBoard(m, board, oldBoard))) {
                playStone();
                return m;
            }
        }
        return new Move();
    }
}
