package ianhblakley.goai.framework;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.framework.scoring.Scorer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds and plays a game
 * Keeps track of the moves played and the ending board
 * Implements {@link Externalizable} so bots don't have to be serializable
 * <p>
 * Created by ian on 10/12/16.
 */
public class Game implements Externalizable {

    private static final Logger logger = LogManager.getFormatterLogger(Game.class);

    private final Scorer scorer;
    // Current board
    private Board board;
    private Bot black;
    private Bot white;
    // Number of elapsed turns
    private int turns;
    // All moves made thus far
    private List<Move> moves;
    // Winner of the game, initially null
    private PositionState winner;

    /**
     * Creates a new game with the black and white bots
     *
     * @param black black bot
     * @param white white bot
     */
    public Game(Bot black, Bot white) {
        this.black = black;
        this.white = white;
        this.board = new Board();
        turns = 0;
        moves = new ArrayList<>();
        logger.info("Initialized Game");
        winner = null;
        scorer = Scorer.getDefaultScorer();
    }

    /**
     * Creates a game from a given board state b and white and black bots
     * @param b board state to resume from
     * @param black black bot
     * @param white white bot
     */
    public Game(Board b, Bot black, Bot white) {
        this.black = black;
        this.white = white;
        this.board = b;
        turns = b.getTurnCount();
        winner = null;
        scorer = Scorer.getDefaultScorer();
    }

    /**
     * Plays the game, querying bots for moves and continuing until both players pass
     * @param verbose whether or not to write log messages
     */
    public void play(boolean verbose) {
        // Used because deserialized games won't have bots
        if (black == null || white == null) {
            logger.error("Can't play games from logs");
            return;
        }
        Move blackMove;
        Move whiteMove;
        int blackCaptured = 0;
        int whiteCaptured = 0;
        do {
            // Blacks Move
            turns++;
            blackMove = black.getPlay(board, turns);
            if (blackMove.isNotPass()) {
                if (verbose) logger.info("Black played move %s on turn %s", blackMove.getPosition(), turns);
                board.placeMove(blackMove);
                if (moves != null) {
                    moves.add(blackMove);
                }
            } else {
                if (verbose) logger.info("Black passed on turn %s", turns);
            }

            // Whites Move
            turns++;
            whiteMove = white.getPlay(board, turns);
            if (whiteMove.isNotPass()) {
                if (verbose) logger.info("White played move %s on turn %s", whiteMove.getPosition(), turns);
                board.placeMove(whiteMove);
                if (moves != null) {
                    moves.add(whiteMove);
                }
            } else {
                if (verbose) logger.info("White passed on turn %s", turns);
            }
            if (verbose) {
                logger.trace("Game Board:\n %s", board);
                if (board.getBlackCaptured() > blackCaptured) {
                    logger.trace("Blacks Captured: %s", board.getBlackCaptured() - blackCaptured);
                    blackCaptured = board.getBlackCaptured();
                }
                if (board.getWhiteCaptured() > whiteCaptured) {
                    logger.trace("Whites Captured: %s", board.getWhiteCaptured() - whiteCaptured);
                    whiteCaptured = board.getWhiteCaptured();
                }
            }
            // Continue until both players pass
        } while (blackMove.isNotPass() || whiteMove.isNotPass());
        this.winner = scorer.winner(board, verbose);
    }

    public PositionState getWinner() {
        return winner;
    }

    public void printStats() {
        logger.info("Moves %s", turns);
        logger.info("Game won by %s with score %s - %s", winner,
                scorer.getBlackScore(), scorer.getWhiteScore());
        logger.info("Final Board" + board.toString());
    }

    /**
     * Writes the game to out stream without having to save the bot objects, only their class names
     * @param out stream to write to
     * @throws IOException thrown if unable to write to out
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(board);
        out.writeInt(turns);
        out.writeObject(moves);
        out.writeObject(white.getClass());
        out.writeObject(black.getClass());
    }

    /**
     * Reads a game from the input stream
     * Read games never have bot objects and can't be played
     * @param in input stream to read from
     * @throws IOException thrown if unable to read from in
     * @throws ClassNotFoundException thrown if unable to instantiate {@link Game}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        board = (Board) in.readObject();
        turns = in.readInt();
        moves = (List<Move>) in.readObject();
        white = null;
        black = null;
    }
}
