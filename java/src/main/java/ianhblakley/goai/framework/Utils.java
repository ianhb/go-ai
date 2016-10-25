package ianhblakley.goai.framework;

import java.util.Arrays;

/**
 * Misc Utils
 * <p>
 * Created by ian on 10/12/16.
 */
public class Utils {
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

    static Board.Cell[][] deepCopyCells(Board.Cell[][] original) {
        if (original == null) {
            return null;
        }

        final Board.Cell[][] result = new Board.Cell[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
            // For Java versions prior to Java 6 use the next:
            // System.arraycopy(original[i], 0, result[i], 0, original[i].length);
        }
        return result;
    }

    public static PositionState getOppositeColor(PositionState color) {
        assert !color.equals(PositionState.EMPTY);
        if (color.equals(PositionState.BLACK)) {
            return PositionState.WHITE;
        } else {
            return PositionState.BLACK;
        }
    }
}
