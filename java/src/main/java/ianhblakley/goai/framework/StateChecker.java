package ianhblakley.goai.framework;

import ianhblakley.goai.Constants;

/**
 * Used to validate whether or not a move is a valid move
 * Includes checks for suicide moves and Ko moves
 * <p>
 * Created by ian on 10/12/16.
 */
public class StateChecker {

    /**
     * Checks if making move results in the piece commiting suicide
     *
     * @param move  potential move
     * @param state current board state
     * @return if the move results in suicide
     */
    private static boolean checkSuicide(Move move, Board state) {
        if (state.getLiberties(move.getPosition()).size() > 0) {
            return false;
        }
        Utils.FourSideFunction checkSides = (board, side, color) -> {
            if (board.getPositionState(side) == PositionState.EMPTY) return 1;
            assert board.getCell(side) != null;
            if (board.getPositionState(side) == color && board.getCell(side).getLibertyCount(board) > 1) return 1;
            return 0;
        };
        return Utils.applyToSideReturn(state, move.getPosition(), move.getColor(), checkSides) > 0;
    }

    /**
     * Checks if making the move will result in KO, or return to a previous gamestate
     * Only checks last move to avoid immediate return to previous state
     * @param move potential move
     * @param state current board state
     * @param oldState board state before last move was made
     * @return if the move results in KO
     */
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

    /**
     * Returns if the move is being played to an empty space
     * @param move potential move
     * @param state current state of game
     * @return if the space is occupied
     */
    private static boolean checkNotEmpty(Move move, Board state) {
        return state.getPositionState(move.getPosition()) != PositionState.EMPTY;
    }

    /**
     * Checks if the move is a legal move
     * Currently checks for suicide and KO
     * @param move potential move
     * @param state current board state
     * @return if the move is legal
     */
    public static boolean isLegalMove(Move move, Board state) {
        return !checkSuicide(move, state) && !checkNotEmpty(move, state);
    }
}
