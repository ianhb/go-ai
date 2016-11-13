package ianhblakley.goai.bots;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.*;
import ianhblakley.goai.neuralnetworkconnection.NeuralNetworkClient;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ian on 11/13/16.
 */
public class NeuralNetBot extends AbstractBot {

    private final NeuralNetworkClient client;

    NeuralNetBot(PositionState color) {
        super(color);
        client = new NeuralNetworkClient(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        if (checkCannotPlay()) return new Move();
        Set<Position> positionSet = board.getAvailableSpaces();
        if (positionSet.size() == 0) return new Move();
        Set<Position> legalMoves = new HashSet<>();
        for (Position p : positionSet) {
            Move m = new Move(p, color);
            if (StateChecker.isLegalMove(m, board)) {
                legalMoves.add(p);
            }
        }
        Position bestMove = client.getBestPosition(color, turnNumber, board, legalMoves);
        playStone();
        return new Move(bestMove, color);
    }
}