package ianhblakley.goai.gui;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.framework.scoring.Scorer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Holds and plays a game between two bots
 *
 * Created by ian on 11/16/16.
 */
class GuiGame extends Task<PositionState> {

    private static final Logger logger = LogManager.getFormatterLogger(GuiGame.class);
    private final Scorer scorer;
    private final Board board;
    private Bot black;
    private Bot white;
    private BoardScene scene;

    private int turns;

    GuiGame(BoardScene scene) {
        this.board = new Board(false);
        this.scene = scene;
        this.scorer = Scorer.getDefaultScorer();
    }

    void setBots(Bot black, Bot white) {
        this.black = black;
        this.white = white;
    }

    @Override
    public PositionState call() throws Exception {
        // Used because deserialized games won't have bots
        if (black == null || white == null) {
            logger.error("Can't play games from logs");
            return null;
        }
        Move blackMove;
        Move whiteMove;
        do {
            // Blacks Move
            blackMove = playMove(black);
            logger.trace("Playing %s", blackMove);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Sleep Error ", e);
                return null;
            }

            // Whites Move
            whiteMove = playMove(white);
            logger.trace("Playing %s", whiteMove);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Sleep Error ", e);
                return null;
            }

            // Continue until both players pass
        } while (blackMove.isNotPass() || whiteMove.isNotPass());
        logger.debug("Game Over");
        return scorer.winner(this.board, false);
    }

    private Move playMove(Bot bot) {
        turns++;
        Move move = bot.getPlay(board, turns);
        if (move.isNotPass()) {
            board.placeMove(move);
            Platform.runLater(new BoardUpdater(move));
        }
        return move;
    }

    void setScene(BoardScene scene) {
        this.scene = scene;
    }

    Scorer getScorer() {
        return scorer;
    }

    class BoardUpdater implements Runnable {

        final Move move;

        BoardUpdater(Move move) {
            this.move = move;
        }

        @Override
        public void run() {
            scene.placeCell(move);
            scene.updateBoard(board);
        }
    }
}
