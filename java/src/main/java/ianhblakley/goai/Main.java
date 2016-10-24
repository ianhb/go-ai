package ianhblakley.goai;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.MctsBot;
import ianhblakley.goai.bots.RandomBot;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;

/**
 * Main class that runs the program
 * <p>
 * Created by ian on 10/12/16.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Bot white = new RandomBot(PositionState.WHITE);
        Bot black = new MctsBot(PositionState.BLACK);
        Game game = new Game(black, white);
        game.play(true);
        game.printStats();
    }
}
