package ianhblakley.goai.framework;

import ianhblakley.goai.Constants;

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

    // Current state of each position of the board
    private PositionState[][] board;
    // Cell that each position is associated with
    // Can either be a Cell or null if position is empty
    private Cell[][] cells;
    // Set of all current cells on the board
    private Set<Cell> cellSet;
    // Number of black pieces on board
    private int blacks;
    // Number of white pieces on board
    private int whites;
    // Number of black pieces captured by white
    private int blackCaptured;
    // Number of white pieces caputred by black
    private int whiteCaptured;
    // Set of all legal moves makable by black
    private Set<Position> blackMoves;
    // Set of all legal moves makable by white
    private Set<Position> whiteMoves;
    // Copy of the previous board state
    private Board previousState;

    public Board() {
        board = new PositionState[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                board[i][j] = PositionState.EMPTY;
            }
        }
        cells = new Cell[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        cellSet = new HashSet<>();
        blacks = 0;
        whites = 0;
        blackCaptured = 0;
        whiteCaptured = 0;
        previousState = null;
        calcLegalMoves();
    }

    /**
     * Set of all open positions on the board
     *
     * @return current open positions
     */
    public Set<Position> getAvailableSpaces() {
        Set<Position> available = new HashSet<>();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (board[i][j].equals(PositionState.EMPTY)) {
                    available.add(new Position(i, j));
                }
            }
        }
        return available;
    }

    /**
     * Places a piece of color color at position position
     * @param color color of piece
     * @param position position of piece
     */
    private void placePiece(PositionState color, Position position) {
        assert !color.equals(PositionState.EMPTY);
        assert getPositionState(position).equals(PositionState.EMPTY);
        board[position.row][position.column] = color;
        if (color.equals(PositionState.BLACK)) blacks++;
        else whites++;
        mergeCells(position);
    }

    /**
     * Returns the current state of the board at position position
     * @param position query position
     * @return state of board at position
     */
    public PositionState getPositionState(Position position) {
        return board[position.row][position.column];
    }

    /**
     * Returns the current state of the board at position (row, column)
     * @param row row of position
     * @param column column of position
     * @return state of baord at position
     */
    public PositionState getPositionState(int row, int column) {
        return getPositionState(new Position(row, column));
    }

    /**
     * Gets the {@link Cell} object at position position
     * Returns null if no cell is at the position
     * @param position query position
     * @return cell object at position or null
     */
    Cell getCell(Position position) {
        return cells[position.row][position.column];
    }

    /**
     * Removes the piece at Position position
     * Throws {@link AssertionError} if the position is {@link PositionState#EMPTY}
     *
     * @param position position to remove piece from
     */
    private void removePosition(Position position) {
        assert !getPositionState(position).equals(PositionState.EMPTY);
        board[position.row][position.column] = PositionState.EMPTY;
        cells[position.row][position.column] = null;
    }

    /**
     * Places move {@link Move} on the board
     * @param move move to play
     */
    public void placeMove(Move move) {
        previousState = deepCopy();
        previousState.previousState = null;
        placePiece(move.getColor(), move.getPosition());
        checkCapture(move.getColor());
    }

    /**
     * Places a move but doesn't record the previous state
     * Used by {@link StateChecker} to check prospective moves
     * @param move potential move
     */
    void placeMoveLight(Move move) {
        placePiece(move.getColor(), move.getPosition());
        checkCapture(move.getColor());
    }

    /**
     * Merges any cells that are adjacent to Position position and are the same color
     * Checks all four sides and merges any cells, updating {@link #cells} and merging {@link #cellSet}
     * @param position position of newly played piece
     */
    private void mergeCells(Position position) {
        Cell cell = new Cell(position);
        cellSet.add(cell);
        FourSideOperation merge = (side, center) -> {
            if (getPositionState(side) == getPositionState(center) && !getCell(side).equals(getCell(center))) {
                getCell(center).merge(getCell(side));
            }
        };
        applyToSide(position, merge);
    }

    /**
     * Returns whether the current board cannot have news played on it
     * @return if the game can't go on
     */
    public boolean isEndGame() {
        return previousState != null &&
                (getTurnCount() == Math.pow(Constants.BOARD_SIZE, 2) ||
                        legalMoves(PositionState.BLACK).size() <= 0 && legalMoves(PositionState.WHITE).size() <= 0);
    }

    /**
     * Applies {@link FourSideOperation#act(Position, Position)} to each of the neighbors of center
     * @param center center position
     * @param operation operation to apply
     */
    private void applyToSide(Position center, FourSideOperation operation) {
        Position left;
        Position right;
        Position up;
        Position down;
        if (center.column > 0) {
            left = new Position(center.row, center.column - 1);
            operation.act(left, center);
        }
        if (center.column < Constants.BOARD_SIZE - 1) {
            right = new Position(center.row, center.column + 1);
            operation.act(right, center);
        }
        if (center.row > 0) {
            up = new Position(center.row - 1, center.column);
            operation.act(up, center);
        }
        if (center.row < Constants.BOARD_SIZE - 1) {
            down = new Position(center.row + 1, center.column);
            operation.act(down, center);
        }
    }

    /**
     * Checks if any cells are captured after a move is played
     * Removes any captured pieces and updates counters
     * Deletes cells that have been captured
     * @param playedColor color of last played piece
     */
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

    /**
     * Returns a deep copy of the board matrix
     * @return deep copy of the board matrix
     */
    private PositionState[][] getBoardCopy() {
        return Utils.deepCopyBoard(board);
    }

    /**
     * Returns the total number of turns played
     * @return turns played
     */
    int getTurnCount() {
        return blackCaptured + blacks + whites + whiteCaptured;
    }

    /**
     * Returns a deep copy of the board
     * {@link #board}, {@link #cells} and {@link #cellSet} are deep copied
     * @return deep copy of this
     */
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
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            string.append(String.format("%1$2s ", i));
        }
        string.append("\n   ");
        string.append(new String(new char[Constants.BOARD_SIZE * 3]).replace('\0', '_'));
        string.append('\n');
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
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
        string.append(new String(new char[Constants.BOARD_SIZE * 3]).replace('\0', '_'));
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

    /**
     * Returns the number of liberties around the the cell containing the Position p
     * Liberties are defined as the open spaces around a cell
     * Cells share liberties
     * Precondition: Position p has a piece played on it
     * @param p position get get liberties for
     * @return number of liberties surrounding the cell
     */
    Set<Position> getLiberties(Position p) {
        Set<Position> possibleEyes = new HashSet<>();
        FourSideOperation liberties = ((side, center) -> {
            if (getPositionState(side) == PositionState.EMPTY) {
                possibleEyes.add(side);
            }
        });
        applyToSide(p, liberties);
        return possibleEyes;
    }

    /**
     * Calculates and updates all legal moves
     */
    private void calcLegalMoves() {
        whiteMoves = legalMoves(PositionState.WHITE);
        blackMoves = legalMoves(PositionState.BLACK);
    }

    /**
     * Returns the let of positions that could be legally played to by PositionState color
     * @param color color of potential move
     * @return all legal moves for color
     */
    private Set<Position> legalMoves(PositionState color) {
        Set<Position> positions = getAvailableSpaces();
        if (positions.size() == 0) {
            return Collections.emptySet();
        }
        Set<Position> legalPositions = new HashSet<>();
        for (Position p : positions) {
            Move m = new Move(p, color);
            if (previousState == null || StateChecker.isLegalMove(m, this, previousState.getBoard())) {
                legalPositions.add(p);
            }
        }
        return legalPositions;
    }

    /**
     * Returns a copy of the board matrix from before the last move was played
     * Retusn null if it is the first play of the game
     * @return copy of previous board matrix or null
     */
    public PositionState[][] getLastBoard() {
        if (previousState == null) {
            return null;
        }
        return previousState.getBoardCopy();
    }

    /**
     * Returns the last calculated {@link #legalMoves(PositionState)} for the given color
     * @param color color of moves
     * @return legal moves for color
     */
    public Set<Position> getLegalMoves(PositionState color) {
        switch (color) {
            case WHITE:
                return whiteMoves;
            case BLACK:
                return blackMoves;
            default:
                return null;
        }
    }

    /**
     * Equality is based on same type and by equality of game board matrix
     */
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

    /**
     * Interface used to abstract a function on two {@link Position}
     */
    interface FourSideOperation {
        void act(Position side, Position center);
    }

    /**
     * Represents a cell on the game board
     * Holds the positions of its pieces, the number of liberties it has and the color of the cell
     */
    class Cell implements Serializable {
        final Set<Position> pieces;
        private int libertyCount;
        private PositionState color;

        /**
         * Creates an empty cell
         */
        Cell() {
            pieces = new HashSet<>();
        }

        /**
         * Creates a new cell containing the piece at init
         * @param init initializing position of the cell
         */
        Cell(Position init) {
            this();
            pieces.add(init);
            color = getPositionState(init);
            Board.this.cells[init.row][init.column] = this;
        }

        PositionState getColor() {
            return color;
        }

        /**
         * Merges the contents of cell1 with this and deletes cell1
         * @param cell1 cell to merge and delete
         */
        void merge(Cell cell1) {
            assert color.equals(cell1.color);
            cellSet.remove(cell1);
            for (Position p : cell1.pieces) {
                assert getPositionState(p).equals(color);
                pieces.add(p);
                Board.this.cells[p.row][p.column] = this;
            }
        }

        /**
         * Deletes a cell from the {@link Board}
         */
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

        /**
         * Calculates the liberties of the cell
         * @return number of liberties
         */
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
