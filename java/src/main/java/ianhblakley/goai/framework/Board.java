package ianhblakley.goai.framework;

import ianhblakley.goai.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Go board
 * <p>
 * Created by ian on 10/12/16.
 */
public class Board implements Serializable {

    private static final Logger logger = LogManager.getFormatterLogger(Board.class);

    // Current state of each position of the board
    private PositionState[][] board;

    private CellManager cellManager;
    // Number of black pieces on board
    private int blacks;
    // Number of white pieces on board
    private int whites;
    // Number of black pieces captured by white
    private int blackCaptured;
    // Number of white pieces caputred by black
    private int whiteCaptured;
    // Copy of the previous board state
    private PositionState[][] previousState;

    private Set<Position> availableSpaces;

    public Board() {
        board = new PositionState[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

        cellManager = new CellManager();
        availableSpaces = new HashSet<>();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                setPositionState(new Position(i, j), PositionState.EMPTY);
                availableSpaces.add(new Position(i, j));
            }
        }
        blacks = 0;
        whites = 0;
        blackCaptured = 0;
        whiteCaptured = 0;
        previousState = null;
    }

    public Board(boolean clean) {
        previousState = null;
    }

    /**
     * Set of all open positions on the board
     *
     * @return current open positions
     */
    public Set<Position> getAvailableSpaces() {
        return availableSpaces;
    }

    /**
     * Places a piece of color color at position position
     * @param color color of piece
     * @param position position of piece
     */
    private void placePiece(PositionState color, Position position) {
        assert !color.equals(PositionState.EMPTY);
        assert getPositionState(position).equals(PositionState.EMPTY);
        changePositionState(position, color);
        cellManager.mergeCells(this, position);
        availableSpaces.remove(position);
        if (color.equals(PositionState.BLACK)) blacks++;
        else whites++;
        assert getPositionState(position) == color;
        assert cellManager.getCell(position) != null;
    }

    /**
     * Returns the current state of the board at position position
     * @param position query position
     * @return state of board at position
     */
    public PositionState getPositionState(Position position) {
        return board[position.getRow()][position.getColumn()];
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

    private void changePositionState(Position position, PositionState state) {
        setPositionState(position, state);
        if (state != PositionState.EMPTY) {
            cellManager.createCell(position, state);
        } else {
            logger.debug("Should delete cell");
        }
    }

    private void setPositionState(Position position, PositionState state) {
        assert getPositionState(position) != state;
        assert position != null;
        board[position.getRow()][position.getColumn()] = state;
    }


    /**
     * Removes the piece at Position position
     * Throws {@link AssertionError} if the position is {@link PositionState#EMPTY}
     *
     * @param position position to remove piece from
     */
    private void removePosition(Position position) {
        assert !getPositionState(position).equals(PositionState.EMPTY);
        if (getPositionState(position).equals(PositionState.BLACK)) {
            blackCaptured++;
            blacks--;
        } else {
            whiteCaptured++;
            whites--;
        }
        changePositionState(position, PositionState.EMPTY);
    }

    void removeCell(Cell cell) {
        cell.getPieces().forEach(this::removePosition);
    }

    Cell getCell(Position p) {
        return cellManager.getCell(p);
    }

    /**
     * Places move {@link Move} on the board
     * @param move move to play
     */
    public void placeMove(Move move) {
        previousState = Utils.deepCopyBoard(board);
        placePiece(move.getColor(), move.getPosition());
        cellManager.checkCapture2(this, move);
    }

    /**
     * Places a move but doesn't record the previous state
     * Used by {@link StateChecker} to check prospective moves
     * @param move potential move
     */
    void placeMoveLight(Move move) {
        placePiece(move.getColor(), move.getPosition());
        cellManager.checkCapture2(this, move);
    }

    /**
     * Returns whether the current board cannot have news played on it
     * @return if the game can't go on
     */
    public boolean isEndGame() {
        return previousState != null &&
                (getTurnCount() == Math.pow(Constants.BOARD_SIZE, 2));
    }


    PositionState[][] getBoard() {
        return board;
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
     * {@link #board} and {@link #cellManager} are deep copied
     * @return deep copy of this
     */
    public Board deepCopy() {
        Board board = new Board(false);
        board.board = Utils.deepCopyBoard(this.board);
        board.cellManager = cellManager.deepCopy();
        board.blacks = blacks;
        board.whites = whites;
        board.blackCaptured = blackCaptured;
        board.whiteCaptured = whiteCaptured;
        board.availableSpaces = new HashSet<>(availableSpaces);
        board.previousState = Utils.deepCopyBoard(previousState);
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
        string.append('\n');
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
        Utils.FourSideOperation liberties = ((board, side, center) -> {
            if (board.getPositionState(side) == PositionState.EMPTY) {
                possibleEyes.add(side);
            }
        });
        Utils.applyToSide(this, p, liberties);
        return possibleEyes;
    }

    /**
     * Returns a copy of the board matrix from before the last move was played
     * Retusn null if it is the first play of the game
     * @return copy of previous board matrix or null
     */
    PositionState[][] getLastBoard() {
        if (previousState == null) {
            return null;
        }
        return Utils.deepCopyBoard(previousState);
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

}