package ianhblakley.goai.framework;

import ianhblakley.goai.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Manages the cells for a {@link Board}
 * <p>
 * Created by ian on 10/31/16.
 */
class CellManager {

    private static final Logger logger = LogManager.getFormatterLogger(CellManager.class);

    // Cell that each position is associated with
    // Can either be a Cell or null if position is empty
    private Cell[][] cellMap;
    // Set of all current cellMap on the board
    private Set<Cell> cellSet;

    CellManager() {
        cellMap = new Cell[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        cellSet = new HashSet<>();
    }

    /*    /**
     * Checks if any cellMap are captured after a move is played
     * Removes any captured pieces and updates counters
     * Deletes cellMap that have been captured
     * @param playedColor color of last played piece
     *//*
    void checkCapture(PositionState playedColor) {
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
    }*/

    void checkCapture2(Board board, Move move) {
        Utils.FourSideOperation checkCapture = (board1, side, center) -> {
            if (board1.getPositionState(side) == Utils.getOppositeColor(board1.getPositionState(center))) {
                int libertyCount = getCell(side).getLibertyCount(board1);
                if (libertyCount == 0) {
                    board1.removeCellFromBoard(getCell(side));
                    delete(getCell(side));
                    assert getCell(side) == null;
                }
            }
        };
        Utils.applyToSide(board, move.getPosition(), checkCapture);
    }

    /**
     * Merges any cellMap that are adjacent to Position position and are the same color
     * Checks all four sides and merges any cellMap, updating {@link #cellMap} and merging {@link #cellSet}
     *
     * @param position position of newly played piece
     */
    void mergeCells(Board board, Position position) {
        assert board.getPositionState(position) != PositionState.EMPTY;
        Utils.FourSideOperation merge = (board1, side, center) -> {
            assert board1.getPositionState(center) != PositionState.EMPTY;
            if (board1.getPositionState(center) == board1.getPositionState(side) &&
                    !getCell(center).equals(getCell(side))) {
                assert getCell(side).getColor() == board1.getPositionState(side);
                merge(getCell(side), getCell(center));
                assert getCell(side).getPieces().size() > 0;
            }
        };
        Utils.applyToSide(board, position, merge);
    }

    /**
     * Gets the {@link Cell} object at position position
     * Returns null if no cell is at the position
     *
     * @param position query position
     * @return cell object at position or null
     */
    Cell getCell(Position position) {
        return cellMap[position.getRow()][position.getColumn()];
    }

    CellManager deepCopy() {
        CellManager copy = new CellManager();
        Set<Cell> cellSet2 = new HashSet<>();
        Cell[][] cellMap2 = new Cell[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (Cell cell : cellSet) {
            Cell copyCell = new Cell(cell);
            cellSet2.add(copyCell);
            for (Position p : cell.getPieces()) {
                cellMap2[p.getRow()][p.getColumn()] = copyCell;
            }
        }
        copy.cellSet = cellSet2;
        copy.cellMap = cellMap2;
        return copy;
    }

    private void merge(Cell kept, Cell deleted) {
        assert kept.getPieces().size() > 0;
        assert deleted.getPieces().size() > 0;
        assert kept.getColor() == deleted.getColor();
        assert !kept.equals(deleted);
        int keptSize = kept.getPieces().size();
        int deletedSize = deleted.getPieces().size();
        int totalSize = keptSize + deletedSize;
        cellSet.remove(deleted);
        logger.debug("Merging cells %s\n and %s", kept, deleted);
        for (Position p : deleted.getPieces()) {
            setCellMapCell(p, kept);
            kept.getPieces().add(p);
            assert kept.getPieces().contains(p);
        }
        StringBuilder builder = new StringBuilder();
        for (Position p : kept.getPieces()) {
            builder.append(" ").append(p);
        }
        logger.debug("Full cell: %s", builder.toString());
        assert kept.getPieces().size() > 0;
        assert kept.getPieces().size() == totalSize;
    }

    private void add(Cell cell, Position position) {
        cell.add(position);
        setCellMapCell(position, cell);
        assert cell.getPieces().size() > 0;
        assert getCell(position) == cell;
        assert cell.getPieces().contains(position);
    }

    Cell createCell(Position position, PositionState color) {
        Cell cell = new Cell(color);
        cellSet.add(cell);
        add(cell, position);
        assert cell.getPieces().size() > 0;
        return cell;
    }

    private void delete(Cell cell) {
        assert cell.getPieces().size() > 0;
        StringBuilder builder = new StringBuilder();
        for (Position p : cell.getPieces()) {
            builder.append(" ").append(p);
        }
        logger.debug("Deleting cell with pieces %s", builder.toString());
        Iterator it = cell.getPieces().iterator();
        while (it.hasNext()) {
            Position piece = (Position) it.next();
            setCellMapCell(piece, null);
            it.remove();
        }
        cellSet.remove(cell);
    }

    private void setCellMapCell(Position position, Cell cell) {
        cellMap[position.getRow()][position.getColumn()] = cell;
    }

    void checkCell(Position p) {
        assert getCell(p) == null;
    }

    Set<Cell> getCellSet() {
        return cellSet;
    }
}
