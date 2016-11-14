package ianhblakley.goai.framework;

import ianhblakley.goai.Constants;

import java.util.Arrays;

/**
 * Misc Utils
 * <p>
 * Created by ian on 10/12/16.
 */
public class Utils {

    /**
     * Returns a deep copy of the gameboard matrix
     *
     * @param original original version of the board
     * @return new copy of the board
     */
    static PositionState[][] deepCopyBoard(PositionState[][] original) {
        if (original == null) {
            return null;
        }

        final PositionState[][] result = new PositionState[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
            // For Java versions prior to Java 6 use the next:
            // System.arraycopy(original[i], 0, result[i], 0, original[i].length);
        }
        return result;
    }

    /**
     * Returns a deep copy of the cell matrix
     * @param original original cell matrix
     * @return new copy of the cells
     */
    static Cell[][] deepCopyCells(Cell[][] original) {
        if (original == null) {
            return null;
        }

        final Cell[][] result = new Cell[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
            // For Java versions prior to Java 6 use the next:
            // System.arraycopy(original[i], 0, result[i], 0, original[i].length);
        }
        return result;
    }

    /**
     * Returns the opposite color of color
     * Throws {@link AssertionError} if color is {@link PositionState#EMPTY}
     * @param color color to invert
     * @return opposite of color
     */
    public static PositionState getOppositeColor(PositionState color) {
        assert !color.equals(PositionState.EMPTY);
        if (color.equals(PositionState.BLACK)) {
            return PositionState.WHITE;
        } else {
            return PositionState.BLACK;
        }
    }

    /**
     * Applies {@link FourSideOperation#act(Board, Position, Position)} to each of the neighbors of center
     * @param board board on which to apply operation
     * @param center    center position
     * @param operation operation to apply
     */
    static void applyToSide(Board board, Position center, FourSideOperation operation) {
        Position left;
        Position right;
        Position up;
        Position down;
        if (center.getColumn() > 0) {
            left = new Position(center.getRow(), center.getColumn() - 1);
            operation.act(board, left, center);
        }
        if (center.getColumn() < Constants.BOARD_SIZE - 1) {
            right = new Position(center.getRow(), center.getColumn() + 1);
            operation.act(board, right, center);
        }
        if (center.getRow() > 0) {
            up = new Position(center.getRow() - 1, center.getColumn());
            operation.act(board, up, center);
        }
        if (center.getRow() < Constants.BOARD_SIZE - 1) {
            down = new Position(center.getRow() + 1, center.getColumn());
            operation.act(board, down, center);
        }
    }

    /**
     * Applies {@link FourSideFunction#act(Board, Position, PositionState)} to each of the neighbors of center
     *
     * @param board    board on which to apply function
     * @param center   center position
     * @param color    color of the piece at center position of board
     * @param function function to apply
     * @return sum of result of function applied to each side
     */
    static int applyToSideReturn(Board board, Position center, PositionState color, FourSideFunction function) {
        Position left;
        Position right;
        Position up;
        Position down;
        int sum = 0;
        if (center.getColumn() > 0) {
            left = new Position(center.getRow(), center.getColumn() - 1);
            sum += function.act(board, left, color);
        }
        if (center.getColumn() < Constants.BOARD_SIZE - 1) {
            right = new Position(center.getRow(), center.getColumn() + 1);
            sum += function.act(board, right, color);
        }
        if (center.getRow() > 0) {
            up = new Position(center.getRow() - 1, center.getColumn());
            sum += function.act(board, up, color);
        }
        if (center.getRow() < Constants.BOARD_SIZE - 1) {
            down = new Position(center.getRow() + 1, center.getColumn());
            sum += function.act(board, down, color);
        }
        return sum;
    }

    /**
     * Interface used to abstract an operation on two {@link Position}
     */
    interface FourSideOperation {
        void act(Board board, Position side, Position center);
    }

    /**
     * Interface used to abstract a function on two {@link Position}
     */
    interface FourSideFunction {
        int act(Board board, Position side, PositionState color);
    }
}
