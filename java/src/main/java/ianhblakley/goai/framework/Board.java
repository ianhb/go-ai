package ianhblakley.goai.framework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Go board
 * <p>
 * Created by ian on 10/12/16.
 */
public class Board {

    private static final Logger logger = LogManager.getFormatterLogger(Board.class);

    private PositionState[][] board;
    private int boardSize;
    private Cell[][] cells;
    private Set<Cell> cellSet;

    Board(int boardSize) {
        this.boardSize = boardSize;
        board = new PositionState[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = PositionState.EMPTY;
            }
        }
        cells = new Cell[boardSize][boardSize];
        cellSet = new HashSet<>();
    }

    public Set<Position> getAvailableSpaces() {
        Set<Position> available = new HashSet<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j].equals(PositionState.EMPTY)) {
                    available.add(new Position(i, j));
                }
            }
        }
        logger.info("Found %s available spaces", available.size());
        return available;
    }

    private void placePiece(PositionState color, Position position) {
        assert !color.equals(PositionState.EMPTY);
        assert getPositionState(position).equals(PositionState.EMPTY);
        board[position.row][position.column] = color;
        mergeCells(position);
    }

    private PositionState getPositionState(Position position) {
        return board[position.row][position.column];
    }

    private Cell getCell(Position position) {
        return cells[position.row][position.column];
    }

    private void removePosition(Position position) {
        assert !getPositionState(position).equals(PositionState.EMPTY);
        board[position.row][position.column] = PositionState.EMPTY;
        cells[position.row][position.column] = null;
    }

    void placeMove(Move move) {
        placePiece(move.getColor(), move.getPosition());
    }

    private void mergeCells(Position position) {
        Cell cell = new Cell(position);
        cellSet.add(cell);
        FourSideOperation merge = (side, center) -> {
            if (getPositionState(side) == getPositionState(center)) {
                cellSet.remove(getCell(side));
                getCell(center).merge(getCell(side));
            }
            return 0;
        };
        applyToSide(position, merge);
    }

    private int applyToSide(Position center, FourSideOperation operation) {
        int sum = 0;
        Position left;
        Position right;
        Position up;
        Position down;
        if (center.column > 0) {
            left = new Position(center.row, center.column - 1);
            sum += operation.act(left, center);
        }
        if (center.column < boardSize - 2) {
            right = new Position(center.row, center.column + 1);
            sum += operation.act(right, center);
        }
        if (center.row > 0) {
            up = new Position(center.row - 1, center.column);
            sum += operation.act(up, center);
        }
        if (center.row < boardSize - 2) {
            down = new Position(center.row + 1, center.column);
            sum += operation.act(down, center);
        }
        return sum;
    }

    void checkCapture() {
        cellSet.stream().filter(cell -> cell.getLibertyCount() == 0).forEach(Cell::delete);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(new String(new char[boardSize * 3]).replace('\0', '_'));
        string.append('\n');
        for (PositionState[] row : board) {
            string.append("|");
            for (PositionState state : row) {
                string.append(" ");
                switch (state) {
                    case EMPTY:
                        string.append(" ");
                        break;
                    case BLACK:
                        string.append("B");
                        break;
                    case WHITE:
                        string.append("W");
                        break;
                }
                string.append(" ");
            }
            string.append("|\n");
        }
        string.append(new String(new char[boardSize * 3]).replace('\0', '_'));
        return string.toString();
    }

    interface FourSideOperation {
        int act(Position side, Position center);
    }

    private class Cell {
        Set<Position> pieces;

        Cell() {
            pieces = new HashSet<>();
        }

        Cell(Position init) {
            this();
            pieces.add(init);
            Board.this.cells[init.row][init.column] = this;
        }

        void merge(Cell cell1) {
            for (Position p : cell1.pieces) {
                pieces.add(p);
                Board.this.cells[p.row][p.column] = this;
            }
        }

        void delete() {
            pieces.forEach(Board.this::removePosition);
        }

        int getLibertyCount() {
            int libertyCount = 0;
            for (Position p : pieces) {
                FourSideOperation liberties = ((side, center) -> {
                    if (getPositionState(side) == PositionState.EMPTY) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
                libertyCount += applyToSide(p, liberties);
            }
            return libertyCount;
        }
    }

}
