package ianhblakley.goai.framework;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Corresponds to a position on the board
 * <p>
 * Created by ian on 10/12/16.
 */
public class Position implements Serializable {
    int row;
    int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Position(Position position) {
        row = position.getRow();
        column = position.getColumn();
    }

    static Set<Position> getAllPositions(int boardSize) {
        Set<Position> positions = new HashSet<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                positions.add(new Position(i, j));
            }
        }
        return positions;
    }

    public Position getEast() {
        if (column > 0) {
            return new Position(row, column-1);
        }
        return null;
    }

    public Position getWest(int cap) {
        if (column < cap - 1) {
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

    public Position getSouth(int cap) {
        if (row < cap - 1) {
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
