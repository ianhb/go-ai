package ianhblakley.goai.mcts;

import ianhblakley.goai.bots.AlphaGoBot;
import ianhblakley.goai.bots.BotFactory;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;

/**
 * Default policy that simulates using a fast neural network
 * <p>
 * Created by ian on 11/24/16.
 */
class AlphaDefaultPolicy extends AbstractDefaultPolicy {

    private AlphaDefaultPolicy(Node leaf, PositionState color) {
        super(leaf, color);
    }

    /**
     * Simulates a game with two {@link ianhblakley.goai.bots.AlphaGoBot.SimBot} instance and returns the winning color
     * Simulation is started at the board state of the node passed in the constructor
     *
     * @return winning color
     */
    @Override
    public PositionState simulate() {
        AlphaGoBot.SimBot black = (AlphaGoBot.SimBot) BotFactory.getBot(PositionState.BLACK, BotFactory.NEURAL_SIM_BOT);
        AlphaGoBot.SimBot white = (AlphaGoBot.SimBot) BotFactory.getBot(PositionState.WHITE, BotFactory.NEURAL_SIM_BOT);
        Board currentBoard = leafNode.getState().deepCopy();
        currentBoard.verifyIntegrity();
        Game simulation = new Game(currentBoard, black, white);
        simulation.play();
        return simulation.getWinner();
    }

    static class AlphaDefaultPolicyFactory implements DefaultPolicy.DefaultPolicyFactory {
        @Override
        public DefaultPolicy getDefaultPolicy(Node n, PositionState color) {
            return new AlphaDefaultPolicy(n, color);
        }
    }
}
