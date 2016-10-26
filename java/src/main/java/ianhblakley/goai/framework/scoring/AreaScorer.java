package ianhblakley.goai.framework.scoring;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Scores the board by the Area scoring method
 * Chinese Rules
 * Created by ian on 10/14/16.
 */
class AreaScorer extends Scorer {

    private static final Logger logger = LogManager.getFormatterLogger(AreaScorer.class);

    private boolean seenBlack;
    private boolean seenWhite;
    private ScoringState[][] states;

    @Override
    void score(Board board, boolean verbose) {
        getArea(board);
        whiteScore += board.getWhites();
        blackScore += board.getBlacks();
        if (verbose) {
            logger.debug(toString());
            logger.debug("Black Area: %s White Area %s", blackScore, whiteScore);
            logger.debug("Black Stones: %s White Stones: %s", board.getBlacks(), board.getWhites());
        }
    }

    private void getArea(Board board) {
        states = createEmptyState();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (states[i][j] == ScoringState.UNCHECKED) {
                    switch (board.getPositionState(i, j)) {
                        case BLACK:
                            states[i][j] = ScoringState.BLACK;
                            break;
                        case WHITE:
                            states[i][j] = ScoringState.WHITE;
                            break;
                        default:
                            try {
                                int score = floodFill2(board, states, i, j);
                                if (states[i][j] == ScoringState.WHITE_TERRITORY) {
                                    whiteScore += score;
                                } else if (states[i][j] == ScoringState.BLACK_TERRITORY) {
                                    blackScore += score;
                                } else {
                                    assert states[i][j] == ScoringState.DAME;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("\n   ");
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            string.append(String.format("%1$2s ", i));
        }
        string.append("\n   ");
        string.append(new String(new char[Constants.BOARD_SIZE * 3]).replace('\0', '_'));
        string.append('\n');
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            ScoringState[] row = states[i];
            string.append(String.format("%1$2s", i)).append("|");
            for (ScoringState state : row) {
                string.append(" ");
                switch (state) {
                    case DAME:
                        string.append("*");
                        break;
                    case BLACK_TERRITORY:
                        string.append("o");
                        break;
                    case WHITE_TERRITORY:
                        string.append("x");
                        break;
                    case BLACK:
                        string.append("B");
                        break;
                    case WHITE:
                        string.append("W");
                        break;
                    case UNCHECKED:
                        string.append("#");
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

    /**
     * Uses the flood fill algorithm to return the area of open space surrounding the Position (row, column)
     * Greedily stores found states in states matrix
     * The {@link ScoringState} of the area can be found by querying states after function call
     *
     * @param board  board to score
     * @param states previously found {@link ScoringState}
     * @param row    row of position to check
     * @param column column of position to check
     * @return area of controlled territory
     * @throws Exception throws an exception if it never finds a stone on the board
     */
    private int floodFill2(Board board, ScoringState[][] states, int row, int column) throws Exception {
        assert board.getPositionState(row, column) == PositionState.EMPTY;
        seenWhite = false;
        seenBlack = false;
        Queue<Position> floodQueue = new ArrayDeque<>();
        Set<Position> areaSet = new HashSet<>();
        floodQueue.add(new Position(row, column));
        while (floodQueue.size() > 0) {
            Position n = floodQueue.poll();
            if (areaSet.contains(n)) continue;
            areaSet.add(n);
            Position north = n.getNorth();
            queueUp(floodQueue, states, north, board);
            Position south = n.getSouth();
            queueUp(floodQueue, states, south, board);
            Position east = n.getEast();
            queueUp(floodQueue, states, east, board);
            Position west = n.getWest();
            queueUp(floodQueue, states, west, board);
        }

        if (seenBlack && seenWhite) {
            for (Position p : areaSet) {
                setState(ScoringState.DAME, states, p);
            }
            return 0;
        } else if (seenBlack) {
            for (Position p : areaSet) {
                setState(ScoringState.BLACK_TERRITORY, states, p);
            }
            return areaSet.size();
        } else if (seenWhite) {
            for (Position p : areaSet) {
                setState(ScoringState.WHITE_TERRITORY, states, p);
            }
            return areaSet.size();
        } else {
            throw new Exception("No stones seen");
        }
    }

    /**
     * Sets the state of position p in states to state
     * @param state state to set cell to
     * @param states scoring state matrix
     * @param p position to set
     */
    private void setState(ScoringState state, ScoringState[][] states, Position p) {
                states[p.getRow()][p.getColumn()] = state;
    }

    /**
     * Checks a {@link Position} p on {@link Board} b for the {@link PositionState}
     * Update the {@link ScoringState[][]} matrix if a stone is found
     * Queues the cell to check if it empty
     * Areas are based on the types of stones found
     * @param queue queue of positions to check
     * @param states previously found states
     * @param p position to check
     * @param b final board
     */
    private void queueUp(Queue<Position> queue, ScoringState[][] states, Position p, Board b) {
        if (p == null) return;
        switch (b.getPositionState(p)) {
            case BLACK:
                seenBlack = true;
                setState(ScoringState.BLACK, states, p);
                break;
            case WHITE:
                seenWhite = true;
                setState(ScoringState.WHITE, states, p);
                break;
            case EMPTY:
                queue.add(p);
                break;
        }
    }

    /**
     * Creates a {@link ScoringState[][]} filled with UNCHECKED
     */
    private ScoringState[][] createEmptyState() {
        ScoringState[][] states = new ScoringState[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (ScoringState[] row : states) {
            for (int i = 0; i < Constants.BOARD_SIZE; i++) {
                row[i] = ScoringState.UNCHECKED;
            }
        }
        return states;
    }

}
