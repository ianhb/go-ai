package ianhblakley.goai.gui;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.BotFactory;
import ianhblakley.goai.framework.PositionState;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main JavaFX GUI Class
 *
 * Created by ian on 11/16/16.
 */
public class Main extends Application {

    private static final Logger logger = LogManager.getFormatterLogger(Main.class);

    private BoardScene boardScene;
    private GuiGame guiGame;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        Bot black = BotFactory.getBot(PositionState.BLACK, BotFactory.botTypes().get(3));
        Bot white = BotFactory.getBot(PositionState.WHITE, BotFactory.botTypes().get(3));
        guiGame = new GuiGame(black, white, boardScene);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("BoardScene Test");
        boardScene = new BoardScene();
        guiGame.setScene(boardScene);
        boardScene.getScene().setOnMouseClicked(mouseEvent -> {
        });
        ObservableList<String> bots = FXCollections.observableArrayList(BotFactory.botTypes());
        ComboBox<String> blackBotSelect = new ComboBox<>(bots);
        ComboBox<String> whiteBotSelect = new ComboBox<>(bots);
        Button start = new Button("Play");
        start.setOnAction(actionEvent -> {
            stage.setScene(boardScene.getScene());
            new Thread(guiGame).start();
        });

        GridPane gridPane = new GridPane();
        gridPane.add(blackBotSelect, 0, 0);
        gridPane.add(whiteBotSelect, 0, 1);
        gridPane.add(start, 0, 2);
        Scene configScene = new Scene(gridPane, 500, 500);
        stage.setScene(configScene);
        stage.show();
    }
}
