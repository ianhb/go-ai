package ianhblakley.goai.framework;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a cell on the game board
 * Holds the positions of its pieces, the number of liberties it has and the color of the cell
 */
class Cell implements Serializable {

    private final Set<Position> pieces;
    private PositionState color;

    /**
     * Creates an empty cell
     */
    private Cell() {
        pieces = new HashSet<>();
    }

    Cell(Cell original) {
        pieces = new HashSet<>(original.getPieces());
        color = original.getColor();
    }

    /**
     * Creates a new cell containing the piece at init
     *
     * @param color color of the cell
     */
    Cell(PositionState color) {
        this();
        this.color = color;
    }

    PositionState getColor() {
        return color;
    }

    Set<Position> getPieces() {
        return pieces;
    }

    void add(Position p) {
        pieces.add(p);
    }

    /**
     * Calculates the liberties of the cell
     *
     * @return number of liberties
     */
    int getLibertyCount(Board board) {
        Set<Position> possibleEyes = new HashSet<>();
        for (Position p : pieces) {
            possibleEyes.addAll(board.getLiberties(p));
        }

        return possibleEyes.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cell\n").append("Color: ").append(color).append("\nPositions: ");
        for (Position p : getPieces()) {
            builder.append(p).append(", ");
        }
        return builder.toString();
    }
}
