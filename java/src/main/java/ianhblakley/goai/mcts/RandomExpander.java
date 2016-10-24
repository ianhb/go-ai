package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Randomly selects a node to expand
 *
 * Created by ian on 10/17/16.
 */
public class RandomExpander implements Expander {

    private static final Logger logger = LogManager.getFormatterLogger(RandomExpander.class);

    @Override
    public State expand(MonteCarloTree tree, State selectedState) {
        if (selectedState.isTerminalState()) {
            return selectedState;
        }
        Board board = selectedState.getBoard();
        PositionState color = selectedState.getBoard().getPreviousMove().getColor();
        Set<Position> positions = board.getAvailableSpaces();
        assert positions.size() > 0;
        List<Position> randomPositions = new ArrayList<>(positions);
        Collections.shuffle(randomPositions);
        for (Position p : randomPositions) {
            Move m = new Move(p, color, 0);
            if (!(StateChecker.checkBoard(m, board, board.getPreviousState().getBoardCopy()))) {
                Board copy = board.deepCopy();
                copy.placeMove(m);
                State s = new State(selectedState, copy);
                selectedState.addChild(s);
                return s;
            }
        }
        logger.error("Not Expanding Tree");
        return null;
    }
}
