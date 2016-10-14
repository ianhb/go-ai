package ianhblakley.goai.framework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private int blacks;
    private int whites;
    private int blackCaptured;
    private int whiteCaptured;

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
        blacks = 0;
        whites = 0;
        blackCaptured = 0;
        whiteCaptured = 0;
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
        if (color.equals(PositionState.BLACK)) blacks++;
        else whites++;
        mergeCells(position);
    }

    public PositionState getPositionState(Position position) {
        return board[position.row][position.column];
    }

    public PositionState getPositionState(int row, int column) {
        return getPositionState(new Position(row, column));
    }

    Cell getCell(Position position) {
        return cells[position.row][position.column];
    }

    private void removePosition(Position position) {
        assert !getPositionState(position).equals(PositionState.EMPTY);
        board[position.row][position.column] = PositionState.EMPTY;
        cells[position.row][position.column] = null;
    }

    void placeMove(Move move) {
        placePiece(move.getColor(), move.getPosition());
        checkCapture(move.getColor());
    }

    private void mergeCells(Position position) {
        Cell cell = new Cell(position);
        cellSet.add(cell);
        FourSideOperation merge = (side, center) -> {
            if (getPositionState(side) == getPositionState(center) && !getCell(side).equals(getCell(center))) {
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
        if (center.column < boardSize - 1) {
            right = new Position(center.row, center.column + 1);
            sum += operation.act(right, center);
        }
        if (center.row > 0) {
            up = new Position(center.row - 1, center.column);
            sum += operation.act(up, center);
        }
        if (center.row < boardSize - 1) {
            down = new Position(center.row + 1, center.column);
            sum += operation.act(down, center);
        }
        return sum;
    }

    private void checkCapture(PositionState playedColor) {
        Set<Cell> deletedCells = new HashSet<>();
        Set<Cell> noLibertyCells = cellSet.stream().filter(cell -> cell.getLibertyCount() == 0).collect(Collectors.toSet());
        if (noLibertyCells.size() > 1) {
            boolean seenBlack = false;
            boolean seenWhite = false;
            for (Cell cell : noLibertyCells) {
                seenBlack = seenBlack || cell.getColor() == PositionState.BLACK;
                seenWhite = seenWhite || cell.getColor() == PositionState.WHITE;
            }
            if (seenBlack && seenWhite) {
                noLibertyCells.stream().filter(cell -> cell.getColor().equals(Utils.getOppositeColor(playedColor)));
            }
        }
        noLibertyCells.forEach(cell -> {
            cell.delete();
            deletedCells.add(cell);
        });

        cellSet.removeAll(deletedCells);
    }

    PositionState[][] getBoard() {
        return board;
    }

    Board deepCopy() {
        Board board = new Board(boardSize);
        board.board = Utils.deepCopyBoard(this.board);
        board.cells = Utils.deepCopyCells(this.cells);
        board.cellSet = new HashSet<>(this.cellSet);
        return board;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("   ");
        for (int i = 0; i < boardSize; i++) {
            string.append(String.format("%1$2s ", i));
        }
        string.append("\n   ");
        string.append(new String(new char[boardSize * 3]).replace('\0', '_'));
        string.append('\n');
        for (int i = 0; i < boardSize; i++) {
            PositionState[] row = board[i];
            string.append(String.format("%1$2s", i)).append("|");
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
        string.append("   ");
        string.append(new String(new char[boardSize * 3]).replace('\0', '_'));
        return string.toString();
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getBlackCaptured() {
        return blackCaptured;
    }

    public int getWhiteCaptured() {
        return whiteCaptured;
    }

    public int getBlacks() {
        return blacks;
    }

    public int getWhites() {
        return whites;
    }

    Set<Position> getLiberties(Position p) {
        Set<Position> possibleEyes = new HashSet<>();
        FourSideOperation liberties = ((side, center) -> {
            if (getPositionState(side) == PositionState.EMPTY) {
                possibleEyes.add(side);
                return 1;
            } else {
                return 0;
            }
        });
        applyToSide(p, liberties);
        return possibleEyes;
    }

    interface FourSideOperation {
        int act(Position side, Position center);
    }

    class Cell {
        Set<Position> pieces;
        private int libertyCount;
        private PositionState color;

        Cell() {
            pieces = new HashSet<>();
        }

        Cell(Position init) {
            this();
            pieces.add(init);
            color = getPositionState(init);
            Board.this.cells[init.row][init.column] = this;
        }

        PositionState getColor() {
            return color;
        }

        void merge(Cell cell1) {
            assert color.equals(cell1.color);
            cellSet.remove(cell1);
            for (Position p : cell1.pieces) {
                assert getPositionState(p).equals(color);
                pieces.add(p);
                Board.this.cells[p.row][p.column] = this;
            }
        }

        void delete() {
            pieces.forEach((position) -> {
                Board.this.removePosition(position);
                if (color.equals(PositionState.BLACK)) {
                    blackCaptured++;
                    blacks--;
                } else {
                    whiteCaptured++;
                    whites--;
                }
            });
        }

        int getLibertyCount() {
            Set<Position> possibleEyes = new HashSet<>();
            for (Position p : pieces) {
                possibleEyes.addAll(getLiberties(p));
            }
            libertyCount = possibleEyes.size();

            return libertyCount;
        }
    }

}
