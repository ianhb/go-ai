package ianhblakley.goai.gui;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Holds a {@link Scene} of a Go board
 *
 * Created by ian on 11/16/16.
 */
class BoardScene {

    private static final Logger logger = LogManager.getFormatterLogger(BoardScene.class);

    private final GridPane grid;
    private final Scene scene;
    private final Circle[][] pieceMap;

    BoardScene() {
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("game-grid");
        for (int i = 0; i < Constants.BOARD_SIZE + 1; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / (Constants.BOARD_SIZE + 1));
            grid.getColumnConstraints().add(columnConstraints);
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / (Constants.BOARD_SIZE + 1));
            grid.getRowConstraints().add(rowConstraints);
        }
        for (int row = 1; row < Constants.BOARD_SIZE + 1; row++) {
            grid.add(new Text(String.valueOf(row - 1)), 0, row);
        }
        for (int column = 1; column < Constants.BOARD_SIZE + 1; column++) {
            grid.add((new Text(String.valueOf(column - 1))), column, 0);
        }
        for (int row = 1; row < Constants.BOARD_SIZE + 1; row++) {
            for (int column = 1; column < Constants.BOARD_SIZE + 1; column++) {
                Pane pane = new Pane();

                pane.getStyleClass().add("game-grid-cell");
                if (row == 1) {
                    pane.getStyleClass().add("first-row");
                }
                if (column == 1) {
                    pane.getStyleClass().add("first-column");
                }
                grid.add(pane, column, row);
            }
        }

        scene = new Scene(grid, 600, 600);
        scene.getStylesheets().add(BoardScene.class.getResource("game.css").toExternalForm());
        pieceMap = new Circle[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
    }

    void placeCell(Move move) {
        NumberBinding binding = Bindings.min(grid.heightProperty(), grid.widthProperty());
        Circle circle = new Circle();
        circle.radiusProperty().bind(binding.divide(Constants.BOARD_SIZE * 3));
        if (move.getColor() == PositionState.BLACK) {
            circle.setFill(Color.BLACK);
        } else {
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
        }
        grid.add(circle, move.getPosition().getColumn() + 1, move.getPosition().getRow() + 1);
        GridPane.setHalignment(circle, HPos.CENTER);
        GridPane.setValignment(circle, VPos.CENTER);
        pieceMap[move.getPosition().getRow()][move.getPosition().getColumn()] = circle;
        logger.trace("Placed Move %s", move.getPosition());
    }

    void updateBoard(Board board) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int column = 0; column < Constants.BOARD_SIZE; column++) {
                switch (board.getPositionState(row, column)) {
                    case EMPTY:
                        if (pieceMap[row][column] != null) {
                            grid.getChildren().remove(pieceMap[row][column]);
                            pieceMap[row][column] = null;
                            logger.debug("Removing piece from %s, %s", row, column);
                        }
                        break;
                    case BLACK:
                        if (pieceMap[row][column] == null) {
                            placeCell(new Move(new Position(row, column), PositionState.BLACK));
                        } else if (pieceMap[row][column].getFill() != Color.BLACK) {
                            pieceMap[row][column].setFill(Color.BLACK);
                        }
                        break;
                    case WHITE:
                        if (pieceMap[row][column] == null) {
                            placeCell(new Move(new Position(row, column), PositionState.WHITE));
                        } else if (pieceMap[row][column].getFill() != Color.WHITE) {
                            pieceMap[row][column].setFill(Color.WHITE);
                        }
                        break;
                }
            }
        }
    }

    Scene getScene() {
        return scene;
    }
}
