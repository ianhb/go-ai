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
    private Board board;
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

    private int getArea(Board board) {
        this.board = board;
        states = createEmptyState(Constants.BOARDSIZE);
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            for (int j = 0; j < Constants.BOARDSIZE; j++) {
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
                                if (states[i][j] == ScoringState.WHITETERRITORY) {
                                    whiteScore += score;
                                } else if (states[i][j] == ScoringState.BLACKTERRITORY) {
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
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("\n   ");
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            string.append(String.format("%1$2s ", i));
        }
        string.append("\n   ");
        string.append(new String(new char[Constants.BOARDSIZE * 3]).replace('\0', '_'));
        string.append('\n');
        for (int i = 0; i < Constants.BOARDSIZE; i++) {
            ScoringState[] row = states[i];
            string.append(String.format("%1$2s", i)).append("|");
            for (ScoringState state : row) {
                string.append(" ");
                switch (state) {
                    case DAME:
                        string.append("*");
                        break;
                    case BLACKTERRITORY:
                        string.append("o");
                        break;
                    case WHITETERRITORY:
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
        string.append(new String(new char[Constants.BOARDSIZE * 3]).replace('\0', '_'));
        return string.toString();
    }

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
            Position south = n.getSouth(Constants.BOARDSIZE);
            queueUp(floodQueue, states, south, board);
            Position east = n.getEast();
            queueUp(floodQueue, states, east, board);
            Position west = n.getWest(Constants.BOARDSIZE);
            queueUp(floodQueue, states, west, board);
        }

        if (seenBlack && seenWhite) {
            for (Position p : areaSet) {
                setState(ScoringState.DAME, states, p);
            }
            return 0;
        } else if (seenBlack) {
            for (Position p : areaSet) {
                setState(ScoringState.BLACKTERRITORY, states, p);
            }
            return areaSet.size();
        } else if (seenWhite) {
            for (Position p : areaSet) {
                setState(ScoringState.WHITETERRITORY, states, p);
            }
            return areaSet.size();
        } else {
            throw new Exception("No stones seen");
        }
    }

    private void setState(ScoringState state, ScoringState[][] states, Position p) {
                states[p.getRow()][p.getColumn()] = state;
    }

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

    private ScoringState[][] createEmptyState(int size) {
        ScoringState[][] states = new ScoringState[size][size];
        for (ScoringState[] row : states) {
            for (int i=0;i<size;i++) {
                row[i] = ScoringState.UNCHECKED;
            }
        }
        return states;
    }

}
