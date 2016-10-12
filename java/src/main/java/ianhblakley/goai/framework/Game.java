package ianhblakley.goai.framework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds and plays a game
 * Keeps track of the moves played and the ending board
 * <p>
 * Created by ian on 10/12/16.
 */
class Game {

    private static final Logger logger = LogManager.getFormatterLogger(Game.class);

    private Board board;
    private Bot black;
    private Bot white;
    private int turns;
    private List<Move> moves;

    public Game(Bot black, Bot white, int boardSize) {
        this.black = black;
        this.white = white;
        this.board = new Board(boardSize);
        turns = 0;
        moves = new ArrayList<>();
    }

    void play() {
        Move blackMove;
        Move whiteMove;
        do {
            turns++;
            do {
                blackMove = black.getPlay(board, turns);
            } while (checkSuicide(blackMove) || checkKO(blackMove));
            board.placeMove(blackMove);
            moves.add(blackMove);
            board.checkCapture();
            turns++;
            logger.info("Black played move %s on turn %i", blackMove.getPosition(), turns);
            do {
                whiteMove = white.getPlay(board, turns);
            } while (checkSuicide(whiteMove) || checkKO(whiteMove));
            board.placeMove(whiteMove);
            moves.add(whiteMove);
            board.checkCapture();
            logger.info("White played move %s on turn %i", whiteMove.getPosition(), turns);
            logger.info("Current Board: \n %s", board.toString());
        } while (!(blackMove.isPass() && whiteMove.isPass()));
    }

    private boolean checkSuicide(Move move) {
        return false;
    }

    private boolean checkKO(Move move) {
        return false;
    }

}
