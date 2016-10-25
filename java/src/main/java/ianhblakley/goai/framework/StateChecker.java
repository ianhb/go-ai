package ianhblakley.goai.framework;

import ianhblakley.goai.Constants;

/**
 * Used to validate whether or not a move is a valid move
 * Includes checks for suicide moves and Ko moves
 * <p>
 * Created by ian on 10/12/16.
 */
public class StateChecker {

    private static boolean checkSuicide(Move move, Board state) {
        if (state.getLiberties(move.getPosition()).size() > 0) {
            return false;
        }
        Board updatedState = state.deepCopy();
        updatedState.placeMoveLight(move);
        return (updatedState.getPositionState(move.getPosition()).equals(PositionState.EMPTY) ||
                updatedState.getCell(move.getPosition()).getLibertyCount() == 0);
    }

    private static boolean checkKO(Move move, Board state, PositionState[][] oldState) {
        if (oldState == null) {
            return false;
        }
        Board updatedState = state.deepCopy();
        updatedState.placeMoveLight(move);
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (updatedState.getBoard()[i][j] != oldState[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isLegalMove(Move move, Board state, PositionState[][] oldState) {
        return !checkKO(move, state, oldState) && !checkSuicide(move, state);
    }
}
