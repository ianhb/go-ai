package ianhblakley.goai.gui;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;

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
        guiGame = new GuiGame(boardScene);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Select Bots to Play");
        boardScene = new BoardScene();
        guiGame.setScene(boardScene);
        ObservableList<String> bots = FXCollections.observableArrayList(BotFactory.botTypes());
        ComboBox<String> blackBotSelect = new ComboBox<>(bots);
        ComboBox<String> whiteBotSelect = new ComboBox<>(bots);
        blackBotSelect.getSelectionModel().select(BotFactory.RANDOM_BOT);
        whiteBotSelect.getSelectionModel().select(BotFactory.RANDOM_BOT);
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        Button start = new Button("Play");
        start.setOnAction(actionEvent -> {
            Bot black = BotFactory.getBot(PositionState.BLACK, blackBotSelect.getValue());
            Bot white = BotFactory.getBot(PositionState.WHITE, whiteBotSelect.getValue());
            logger.info("Starting game between %s and %s", black, white);
            guiGame.setBots(black, white);
            stage.setScene(boardScene.getScene());
            stage.setTitle("Black: " + blackBotSelect.getValue() + " vs. White: " + whiteBotSelect.getValue());
            guiGame.setOnSucceeded(workerStateEvent -> {
                PositionState result = guiGame.getValue();
                assert result != null;
                Stage winnerPopup = new Stage();
                winnerPopup.initModality(Modality.APPLICATION_MODAL);
                winnerPopup.initOwner(stage);
                VBox dialog = new VBox();
                dialog.getChildren().add(new Text("Winner: " + result + "\n Score: BLACK: " +
                        guiGame.getScorer().getBlackScore() + " -- WHITE: " + guiGame.getScorer().getWhiteScore()));
                Scene dialogScene = new Scene(dialog, 200, 50);
                winnerPopup.setScene(dialogScene);
                winnerPopup.show();
            });
            executorService.execute(guiGame);
            stage.setOnCloseRequest(windowEvent -> executorService.shutdownNow());
        });

        GridPane gridPane = new GridPane();
        gridPane.add(blackBotSelect, 0, 0);
        gridPane.add(whiteBotSelect, 0, 1);
        gridPane.add(start, 0, 2);
        Scene configScene = new Scene(gridPane, 200, 75);
        stage.setScene(configScene);
        stage.show();
    }
}
