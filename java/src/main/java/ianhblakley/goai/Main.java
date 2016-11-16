package ianhblakley.goai;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.BotFactory;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class that runs the program
 * <p>
 * Created by ian on 10/12/16.
 */
class Main {

    private static final Logger logger = LogManager.getFormatterLogger(Main.class);

    public static void main(String[] args) throws Exception {
        List<String> winnerStats = new ArrayList<>();
        List<String> botSet = BotFactory.botTypes();
        for (int i = 0; i < botSet.size(); i++) {
            for (int j = i + 1; j < botSet.size(); j++) {
                int wins = 0;
                for (int rounds = 0; rounds < 10; rounds++) {
                    Bot black = BotFactory.getBot(PositionState.BLACK, botSet.get(i));
                    Bot white = BotFactory.getBot(PositionState.WHITE, botSet.get(j));
                    PositionState winner = playGame(black, white, true);
                    if (winner == PositionState.BLACK) {
                        wins++;
                    }
                }
                winnerStats.add("Game between " + botSet.get(i) + " and " + botSet.get(j) + " had black win " + wins +
                        " times");

            }
        }

        winnerStats.forEach(logger::info);
    }

    private static PositionState playGame(Bot black, Bot white, boolean verbose) {
        logger.info("Starting game between %s and %s", black, white);
        Game game = new Game(black, white);
        game.play(verbose);
        logger.info("Game Finished: %s vs %s", black, white);
        game.printStats();
        return game.getWinner();
    }
}
