package ianhblakley.goai.framework;

import ianhblakley.goai.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a Go board
 * <p>
 * Created by ian on 10/12/16.
 */
public class Board implements Serializable {

    private static final Logger logger = LogManager.getFormatterLogger(Board.class);

    private PositionState[][] board;
    private Cell[][] cells;
    private Set<Cell> cellSet;
    private int blacks;
    private int whites;
    private int blackCaptured;
    private int whiteCaptured;
    private Board previousState;

    public Board() {
        board = new PositionState[Constants.BOARDSIZE][Constants.BOARDSIZE];
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            for (int j = 0; j < Constants.BOARDSIZE; j++) {
                board[i][j] = PositionState.EMPTY;
            }
        }
        cells = new Cell[Constants.BOARDSIZE][Constants.BOARDSIZE];
        cellSet = new HashSet<>();
        blacks = 0;
        whites = 0;
        blackCaptured = 0;
        whiteCaptured = 0;
        previousState = null;
    }

    public Set<Position> getAvailableSpaces() {
        Set<Position> available = new HashSet<>();
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            for (int j = 0; j < Constants.BOARDSIZE; j++) {
                if (board[i][j].equals(PositionState.EMPTY)) {
                    available.add(new Position(i, j));
                }
            }
        }
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

    public void placeMove(Move move) {
        placePiece(move.getColor(), move.getPosition());
        checkCapture(move.getColor());
        previousState = deepCopy();
        previousState.previousState = null;
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

    public boolean isEndGame() {
        if (previousState == null) {
            return false;
        }
        if (getTurnCount() == Math.pow(Constants.BOARDSIZE, 2)) {
            return true;
        }
        if (legalMoves(PositionState.BLACK).size() > 0) {
            return false;
        }
        if (legalMoves(PositionState.WHITE).size() > 0) {
            return false;
        }
        return true;
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
        if (center.column < Constants.BOARDSIZE - 1) {
            right = new Position(center.row, center.column + 1);
            sum += operation.act(right, center);
        }
        if (center.row > 0) {
            up = new Position(center.row - 1, center.column);
            sum += operation.act(up, center);
        }
        if (center.row < Constants.BOARDSIZE - 1) {
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

    private PositionState[][] getBoardCopy() {
        return Utils.deepCopyBoard(board);
    }

    int getTurnCount() {
        return blackCaptured + blacks + whites + whiteCaptured;
    }

    public Board deepCopy() {
        Board board = new Board();
        board.board = Utils.deepCopyBoard(this.board);
        board.cells = Utils.deepCopyCells(this.cells);
        board.cellSet = new HashSet<>(this.cellSet);
        return board;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("\n");
        string.append("   ");
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            string.append(String.format("%1$2s ", i));
        }
        string.append("\n   ");
        string.append(new String(new char[Constants.BOARDSIZE * 3]).replace('\0', '_'));
        string.append('\n');
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
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
        string.append(new String(new char[Constants.BOARDSIZE * 3]).replace('\0', '_'));
        return string.toString();
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

    public Set<Position> legalMoves(PositionState color) {
        Set<Position> positions = getAvailableSpaces();
        if (positions.size() == 0) {
            return Collections.emptySet();
        }
        Set<Position> legalPositions = new HashSet<>();
        for (Position p : positions) {
            Move m = new Move(p, color);
            if (previousState == null || !StateChecker.checkBoard(m, this, previousState.getBoard())) {
                legalPositions.add(p);
            }
        }
        return legalPositions;
    }

    public PositionState[][] getLastBoard() {
        if (previousState == null) {
            return null;
        }
        return previousState.getBoardCopy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board1 = (Board) o;
        return Arrays.deepEquals(board, board1.board);

    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    interface FourSideOperation {
        int act(Position side, Position center);
    }

    class Cell implements Serializable {
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
