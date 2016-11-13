package ianhblakley.goai;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.RandomBot;
import ianhblakley.goai.bots.RandomMCTSBot;
import ianhblakley.goai.bots.UctBot;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class that runs the program
 * <p>
 * Created by ian on 10/12/16.
 */
class Main {

    private static final Logger logger = LogManager.getFormatterLogger(Main.class);

    public static void main(String[] args) throws Exception {

        Bot black = new RandomBot(PositionState.BLACK);
        Bot white = new UctBot(PositionState.WHITE);
        Game game = new Game(black, white);
        game.play(true);
        logger.info("Game: Random vs UCT");
        game.printStats();

        black = new RandomBot(PositionState.BLACK);
        white = new RandomMCTSBot(PositionState.WHITE);
        game = new Game(black, white);
        game.play(false);
        logger.info("Game: Random vs MCTS");
        game.printStats();

        black = new RandomMCTSBot(PositionState.BLACK);
        white = new UctBot(PositionState.WHITE);
        game = new Game(black, white);
        game.play(false);
        logger.info("Game: MCTS vs UCT");
        game.printStats();

    }
}
