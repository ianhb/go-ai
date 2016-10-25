package ianhblakley.goai.framework;

import java.io.Serializable;

/**
 * Corresponds to a position on the board
 * <p>
 * Created by ian on 10/12/16.
 */
public class Position implements Serializable {
    final int row;
    final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Position getEast() {
        if (column > 0) {
            return new Position(row, column-1);
        }
        return null;
    }

    public Position getWest() {
        if (column < ianhblakley.goai.Constants.BOARD_SIZE - 1) {
            return new Position(row, column+1);
        }
        return null;
    }

    public Position getNorth() {
        if (row > 0) {
            return new Position(row-1, column);
        }
        return null;
    }

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
