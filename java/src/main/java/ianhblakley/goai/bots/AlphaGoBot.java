package ianhblakley.goai.bots;

import ianhblakley.goai.framework.*;
import ianhblakley.goai.mcts.MCTS;
import ianhblakley.goai.neuralnetworkconnection.NeuralNetworkClient;

import java.util.HashSet;
import java.util.Set;

/**
 * Bot that uses the Google Alpha-Go strategy of combining neural networks and MCTS to select a move
 * <p>
 * Created by ian on 11/24/16.
 */
public class AlphaGoBot extends MCTSBot {

    AlphaGoBot(PositionState color) {
        super(color);
        mcts = MCTS.alphaMCTS(color);
    }

    @Override
    public String toString() {
        return "Alpha Go Bot";
    }

    public static class SimBot extends AbstractBot {

        private NeuralNetworkClient client;

        SimBot(PositionState color) {
            super(color);
        }

        public void setClient(NeuralNetworkClient client) {
            this.client = client;
        }

        @Override
        public Move getPlay(Board board, int turnNumber) {
            if (checkCannotPlay()) return new Move(color);
            Set<Position> positionSet = board.getAvailableSpaces();
            if (positionSet.size() == 0) return new Move(color);
            Set<Position> legalMoves = new HashSet<>();
            for (Position p : positionSet) {
                Move m = new Move(p, color);
                if (StateChecker.isLegalMove(m, board)) {
                    legalMoves.add(p);
                }
            }
            Position bestMove = client.getSimulationPosition(color, turnNumber, board, legalMoves);
            if (bestMove == null) {
                return new Move(color);
            }
            playStone();
            return new Move(bestMove, color);
        }

    }
}
