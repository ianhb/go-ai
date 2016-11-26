package ianhblakley.goai.gui;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.BotFactory;
import ianhblakley.goai.framework.PositionState;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by ian on 11/16/16.
 */
public class Main extends Application {

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
        stage.setScene(boardScene.getScene());
        stage.show();
    }
}
