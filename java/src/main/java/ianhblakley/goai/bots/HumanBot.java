package ianhblakley.goai.bots;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Bot that takes console input from a human and plays the given move
 *
 * Created by ian on 11/16/16.
 */
public class HumanBot extends AbstractBot {

    private static final Logger logger = LogManager.getFormatterLogger(HumanBot.class);

    private final BufferedReader inputReader;

    HumanBot(PositionState color) {
        super(color);
        inputReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Player Moves are input from the console in the format: row column");
        System.out.println("To pass, input 'pass'");
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        System.out.println("Current Board State");
        System.out.println(board);
        if (checkCannotPlay()) {
            System.out.println("Player doesn't have any stones to play");
            return new Move();
        }
        System.out.println("Player Move: ");
        while (true) {
            try {
                String input = inputReader.readLine();
                if (input.contains("pass")) {
                    System.out.println("Player Passing");
                    return new Move();
                }
                assert input.contains(" ");
                int row = Integer.parseInt(input.substring(0, input.indexOf(" ")));
                int column = Integer.parseInt(input.substring(input.indexOf(" ") + 1));
                logger.debug("Move: %s %s", row, column);
                assert row < Constants.BOARD_SIZE;
                assert row >= 0;
                assert column < Constants.BOARD_SIZE;
                assert column >= 0;
                System.out.println("Playing board");
                playStone();
                return new Move(new Position(row, column), color);
            } catch (IOException e) {
                logger.error("Input Read Error", e);
            } catch (AssertionError e) {
                System.out.println("Move input incorrectly");
                logger.error("Input format error", e);
            }
        }
    }
}
