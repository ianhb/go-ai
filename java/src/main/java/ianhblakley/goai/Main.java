package ianhblakley.goai;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.BotFactory;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.neuralnetworkconnection.GameLoggerClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class that runs the program
 * <p>
 * Created by ian on 10/12/16.
 */
class Main {

    private static final Logger logger = LogManager.getFormatterLogger(Main.class);
    private static GameLoggerClient client = GameLoggerClient.getInstance();

    public static void main(String[] args) throws Exception {
        List<String> botSet = BotFactory.botTypes();
        Map<Matchup, Integer> logs = new HashMap<>();
        for (int rounds = 0; rounds < 10; rounds++) {
            for (int i = 0; i < botSet.size(); i++) {
                for (int j = i + 1; j < botSet.size(); j++) {
                    Bot black = BotFactory.getBot(PositionState.BLACK, botSet.get(i));
                    Bot white = BotFactory.getBot(PositionState.WHITE, botSet.get(j));
                    PositionState winner = playGame(black, white);
                    Matchup matchup = new Matchup(black.getClass(), white.getClass());
                    if (logs.containsKey(matchup)) {
                        if (winner == PositionState.BLACK) {
                            logs.put(matchup, logs.get(matchup) + 1);
                        }
                    } else {
                        if (winner == PositionState.BLACK) {
                            logs.put(matchup, 1);
                        } else {
                            logs.put(matchup, 0);
                        }
                    }
                }
            }
            printGames(logs, rounds);
        }
    }

    private static void printGames(Map<Matchup, Integer> logs, int rounds) {
        logger.info("Round %s", rounds);
        for (Map.Entry<Matchup, Integer> matchup : logs.entrySet()) {
            logger.info("Game between %s and %s", matchup.getKey().black.getCanonicalName(), matchup.getKey().white.getCanonicalName());
            logger.info("Score: %s : %s", matchup.getValue(), rounds - matchup.getValue());
        }
    }

    private static PositionState playGame(Bot black, Bot white) {
        logger.info("Starting game between %s and %s", black, white);
        Game game = new Game(black, white, true);
        game.play();
        logger.info("Game Finished: %s vs %s", black, white);
        game.printStats();
        if (Constants.LOG_GAMES) {
            client.logGame(game);
        }
        return game.getWinner();
    }

    private static class Matchup {
        Class black;
        Class white;

        Matchup(Class black, Class white) {
            this.black = black;
            this.white = white;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Matchup matchup = (Matchup) o;

            if (black != null ? !black.equals(matchup.black) : matchup.black != null) return false;
            return white != null ? white.equals(matchup.white) : matchup.white == null;
        }

        @Override
        public int hashCode() {
            int result = black != null ? black.hashCode() : 0;
            result = 31 * result + (white != null ? white.hashCode() : 0);
            return result;
        }
    }
}
