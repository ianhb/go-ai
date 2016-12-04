package ianhblakley.goai.bots;

import ianhblakley.goai.framework.*;
import ianhblakley.goai.neuralnetworkconnection.NeuralNetworkClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Bot that uses {@link NeuralNetworkClient} to find the best move given the current game state
 *
 * Created by ian on 11/13/16.
 */
class NeuralNetBot extends AbstractBot {

    private final NeuralNetworkClient client;

    NeuralNetBot(PositionState color) {
        super(color);
        client = NeuralNetworkClient.getInstance();
    }

    @Override
    public Move getPlay(Board board, int turnNumber) {
        if (checkCannotPlay()) return new Move(color);
        Set<Position> positionSet = board.getAvailableSpaces();

        if (positionSet.size() == 0) return new Move(color);
        List<Position> legalMoves = new ArrayList<>();
        for (Position p : positionSet) {
            Move m = new Move(p, color);
            if (StateChecker.isLegalMove(m, board)) {
                legalMoves.add(p);
            }
        }
        if (legalMoves.size() < 100) {
            legalMoves.add(null);
        }
        Position bestMove = client.getBestPosition(color, board, legalMoves);
        if (bestMove == null) {
            return new Move(color);
        }
        playStone();
        return new Move(bestMove, color);
    }

    @Override
    public String toString() {
        return "Neural Net Bot";
    }

}
