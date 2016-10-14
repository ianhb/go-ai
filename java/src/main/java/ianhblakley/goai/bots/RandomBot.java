package ianhblakley.goai.bots;

import ianhblakley.goai.framework.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Random Bot that selects a random open space to play on
 * <p>
 * Created by ian on 10/12/16.
 */
public class RandomBot implements Bot {

    private final static Logger logger = LogManager.getFormatterLogger(RandomBot.class);

    private final PositionState color;
    private int stones;

    public RandomBot(PositionState color, int boardSize) {
        assert color != null;
        assert !color.equals(PositionState.EMPTY);
        this.color = color;
        stones = (int) (Math.floor(Math.pow(boardSize, 2)) / 2);
        if (color.equals(PositionState.BLACK)) stones++;
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
                stones--;
                return m;
            }
        }
        return new Move();
    }

    @Override
    public boolean checkCanPlay() {
        return stones > 0;
    }
}
