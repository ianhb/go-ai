package ianhblakley.goai;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.RandomBot;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;

/**
 * Main class that runs the program
 * <p>
 * Created by ian on 10/12/16.
 */
public class Main {

    private static final int BOARDSIZE = 9;

    public static void main(String[] args) {
        Bot white = new RandomBot(PositionState.WHITE, BOARDSIZE);
        Bot black = new RandomBot(PositionState.BLACK, BOARDSIZE);
        Game game = new Game(black, white, BOARDSIZE);
        game.play();
        game.printStats();
    }
}
