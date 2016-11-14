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
    private PositionState[][] boardMap;

    private CellManager cellManager;
    // Number of black pieces on board
    private int blacks;
    // Number of white pieces on board
    private int whites;
    // Number of black pieces captured by white
    private int blackCaptured;
    // Number of white pieces caputred by black
    private int whiteCaptured;
    // Copy of the previous boardMap state
    private PositionState[][] previousState;

    private Set<Position> availableSpaces;

    public Board(boolean clean) {
        if (clean) {
            previousState = null;
            return;
        }
        boardMap = new PositionState[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

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

    /**
     * Set of all open positions on the boardMap
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
        availableSpaces.remove(position);
        if (color.equals(PositionState.BLACK)) {
            blacks++;
        } else {
            whites++;
        }
        assert getPositionState(position) == color;
        assert cellManager.getCell(position) != null;
    }

    /**
     * Returns the current state of the boardMap at position position
     * @param position query position
     * @return state of boardMap at position
     */
    public PositionState getPositionState(Position position) {
        return boardMap[position.getRow()][position.getColumn()];
    }

    /**
     * Returns the current state of the boardMap at position (row, column)
     * @param row row of position
     * @param column column of position
     * @return state of baord at position
     */
    public PositionState getPositionState(int row, int column) {
        return getPositionState(new Position(row, column));
    }

    /**
     * Changes a state of the boardMap to state
     * Uses {@link CellManager} to create and merge affected cells
     *
     * @param position position to change
     * @param state    state to change to
     */
    private void changePositionState(Position position, PositionState state) {
        setPositionState(position, state);
        if (state != PositionState.EMPTY) {
            cellManager.createCell(position, state);
            cellManager.mergeCells(this, position);
        }
        if (getPositionState(position) != PositionState.EMPTY) {
            assert cellManager.getCell(position).getColor() == state;
        }
    }

    /**
     * Sets the position on boardMap to state
     * posiion != null
     * state != EMPTY
     *
     * @param position position to change
     * @param state    state to change to
     */
    private void setPositionState(Position position, PositionState state) {
        assert getPositionState(position) != state;
        assert position != null;
        boardMap[position.getRow()][position.getColumn()] = state;
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

    /**
     * Remove the given cell from the boardMap, updating counts of moves and checking that deletion didn't
     * make the boardMap into an inconsistent state
     * @param cell cell to delete
     */
    void removeCellFromBoard(Cell cell) {
        assert cell != null;
        int turnCount = getTurnCount();
        int removeCount = cell.getPieces().size();
        int currentCaptured;
        int currentPlayed;
        if (cell.getColor() == PositionState.BLACK) {
            currentCaptured = blackCaptured;
            currentPlayed = blacks;
        } else {
            currentCaptured = whiteCaptured;
            currentPlayed = whites;
        }
        cell.getPieces().forEach(this::removePosition);
        assert getTurnCount() == turnCount;
        if (cell.getColor() == PositionState.BLACK) {
            assert blacks == currentPlayed - removeCount;
            assert blackCaptured == currentCaptured + removeCount;
        } else {
            assert whites == currentPlayed - removeCount;
            assert whiteCaptured == currentCaptured + removeCount;
        }

    }

    Cell getCell(Position p) {
        return cellManager.getCell(p);
    }

    private Cell getCell(int row, int column) {
        return cellManager.getCell(new Position(row, column));
    }

    /**
     * Places move {@link Move} on the board
     * @param move move to play
     */
    public void placeMove(Move move) {
        previousState = Utils.deepCopyBoard(boardMap);
        placePiece(move.getColor(), move.getPosition());
        cellManager.checkCapture2(this, move);
        verifyIntegrity();
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


    PositionState[][] getBoardMap() {
        return boardMap;
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
     * {@link #boardMap} and {@link #cellManager} are deep copied
     * @return deep copy of this
     */
    public Board deepCopy() {
        Board board = new Board(false);
        board.boardMap = Utils.deepCopyBoard(this.boardMap);
        board.cellManager = cellManager.deepCopy();
        board.blacks = blacks;
        board.whites = whites;
        board.blackCaptured = blackCaptured;
        board.whiteCaptured = whiteCaptured;
        board.availableSpaces = new HashSet<>(availableSpaces);
        board.previousState = Utils.deepCopyBoard(previousState);
        return board;
    }

    /**
     * Verify the integrity of the board to try to catch inconsistencies
     */
    public void verifyIntegrity() {
        if (Constants.VERIFY_STATES) {
            for (int row = 0; row < Constants.BOARD_SIZE; row++) {
                for (int column = 0; column < Constants.BOARD_SIZE; column++) {
                    if (getPositionState(row, column) != PositionState.EMPTY) {
                        assert getCell(row, column) != null;
                    }
                }
            }
            int blackCells = 0;
            int whiteCells = 0;
            for (Cell cell : cellManager.getCellSet()) {
                if (cell.getColor() == PositionState.BLACK) {
                    blackCells += cell.getPieces().size();
                } else {
                    whiteCells += cell.getPieces().size();
                }
            }
            assert blacks == blackCells;
            assert whites == whiteCells;
        }
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
            PositionState[] row = boardMap[i];
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
     * Equality is based on same type and by equality of game boardMap matrix
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board1 = (Board) o;
        return Arrays.deepEquals(boardMap, board1.boardMap);

    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardMap);
    }

}
