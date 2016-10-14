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
 * <p>
 * Created by ian on 10/12/16.
 */
public class Game implements Externalizable {

    private static final Logger logger = LogManager.getFormatterLogger(Game.class);

    private Board board;
    private Bot black;
    private Bot white;
    private int turns;
    private List<Move> moves;

    public Game() {}

    public Game(Bot black, Bot white, int boardSize) {
        this.black = black;
        this.white = white;
        this.board = new Board(boardSize);
        turns = 0;
        moves = new ArrayList<>();
        logger.info("Initialized Game");
    }

    public void play() throws Exception {
        if (black == null || white == null) {
            throw new Exception("Can't play games from log");
        }
        Move blackMove;
        Move whiteMove;
        PositionState[][] oldBoard = null;
        do {
            turns++;
            blackMove = black.getPlay(board, oldBoard, turns);
            oldBoard = Utils.deepCopyBoard(board.getBoard());
            if (!blackMove.isPass()) {
                logger.info("Black played move %s on turn %s", blackMove.getPosition(), turns);
                board.placeMove(blackMove);
                moves.add(blackMove);
            } else {
                logger.info("Black passed on turn %s", turns);
            }

            turns++;

            whiteMove = white.getPlay(board, oldBoard, turns);
            oldBoard = Utils.deepCopyBoard(board.getBoard());
            if (!whiteMove.isPass()) {
                logger.info("White played move %s on turn %s", whiteMove.getPosition(), turns);
                board.placeMove(whiteMove);
                moves.add(whiteMove);
            } else {
                logger.info("White passed on turn %s", turns);
            }
        } while (!(blackMove.isPass() && whiteMove.isPass()));
        Scorer scorer = Scorer.getDefaultScorer();
        logger.info("Game won by %s with score %s - %s", scorer.winner(board),
                scorer.getBlackScore(), scorer.getWhiteScore());
    }

    public void printStats() {
        logger.info("Moves %s", turns);
        logger.info("Final Board \n" + board.toString());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(board);
        out.writeInt(turns);
        out.writeObject(moves);
        out.writeObject(white.getClass());
        out.writeObject(black.getClass());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        board = (Board) in.readObject();
        turns = in.readInt();
        moves = (List<Move>) in.readObject();
        white = null;
        black = null;
    }
}
