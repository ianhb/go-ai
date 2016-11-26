package ianhblakley.goai.gui;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.framework.scoring.Scorer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by ian on 11/16/16.
 */
class GuiGame {

    private static final Logger logger = LogManager.getFormatterLogger(GuiGame.class);

    private final Bot black;
    private final Bot white;

    private final Scorer scorer;

    private final Board board;
    private final BoardScene scene;

    private int turns;

    GuiGame(Bot black, Bot white, BoardScene scene) {
        this.black = black;
        this.white = white;
        this.board = new Board(false);
        this.scene = scene;
        this.scorer = Scorer.getDefaultScorer();
    }

    /**
     * Plays the game, querying bots for moves and continuing until both players pass
     */
    public void play() {
        // Used because deserialized games won't have bots
        if (black == null || white == null) {
            logger.error("Can't play games from logs");
            return;
        }
        Move blackMove;
        Move whiteMove;
        do {
            // Blacks Move
            blackMove = playMove(black);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                logger.error("Sleep Error ", e);
            }

            // Whites Move
            whiteMove = playMove(white);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                logger.error("Sleep Error ", e);
            }

            // Continue until both players pass
        } while (blackMove.isNotPass() || whiteMove.isNotPass());
        PositionState winner = scorer.winner(this.board, false);
        scene.displayWinner(winner);
    }

    private Move playMove(Bot bot) {
        turns++;
        Move move = bot.getPlay(board, turns);
        if (move.isNotPass()) {
            board.placeMove(move);
            board.verifyIntegrity();
        }
        scene.placeCell(move);
        scene.updateBoard(board);
        return move;
    }
}
