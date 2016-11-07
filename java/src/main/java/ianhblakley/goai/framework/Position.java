package ianhblakley.goai.framework;

import java.io.Serializable;

/**
 * Corresponds to a position on the board
 * Contains a row and column, which correspond to the
 * first and second indices of the {@link Board#board} or {@link CellManager#cellMap}
 * <p>
 * Created by ian on 10/12/16.
 */
public class Position implements Serializable {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the east neighbor of this position if it exists
     * Returns null if the node is on the east edge
     *
     * @return east neighbor if it exists
     */
    public Position getEast() {
        if (column > 0) {
            return new Position(row, column-1);
        }
        return null;
    }

    /**
     * Returns the west neighbor of this position if it exists
     * Returns null if the node is on the west edge
     * @return west neighbor if it exists
     */
    public Position getWest() {
        if (column < ianhblakley.goai.Constants.BOARD_SIZE - 1) {
            return new Position(row, column+1);
        }
        return null;
    }

    /**
     * Returns the north neighbor of this position if it exists
     * Returns null if the node is on the north edge
     * @return north neighbor if it exists
     */
    public Position getNorth() {
        if (row > 0) {
            return new Position(row-1, column);
        }
        return null;
    }

    /**
     * Returns the south neighbor of this position if it exists
     * Returns null if the node is on the south edge
     * @return south neighbor if it exists
     */
    public Position getSouth() {
        if (row < ianhblakley.goai.Constants.BOARD_SIZE - 1) {
            return new Position(row+1, column);
        }
        return null;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    /**
     * Equality of nodes is equal to the equality of the row and column values
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }
}
