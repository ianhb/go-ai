package ianhblakley.goai.framework;

import java.util.HashSet;
import java.util.Set;

/**
 * Corresponds to a position on the board
 * <p>
 * Created by ian on 10/12/16.
 */
public class Position {
    int row;
    int column;

    Position(int row, int column) {
        this.row = row;
        this.column = column;
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
