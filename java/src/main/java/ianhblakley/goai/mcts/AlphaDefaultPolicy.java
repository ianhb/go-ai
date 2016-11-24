package ianhblakley.goai.mcts;

import ianhblakley.goai.bots.Bot;
import ianhblakley.goai.bots.BotFactory;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;

/**
 * Default policy that simulates using a fast neural network
 * <p>
 * Created by ian on 11/24/16.
 */
class AlphaDefaultPolicy implements DefaultPolicy {

    private final Node leafNode;
    private final PositionState color;

    private AlphaDefaultPolicy(Node leaf, PositionState color) {
        this.leafNode = leaf;
        this.color = color;
    }

    /**
     * Simulates a game with two {@link ianhblakley.goai.bots.AlphaGoBot.SimBot} instance and returns the winning color
     * Simulation is started at the board state of the node passed in the constructor
     *
     * @return winning color
     */
    @Override
    public PositionState simulate() {
        Bot black = BotFactory.getBot(PositionState.BLACK, BotFactory.NEURAL_SIM_BOT);
        Bot white = BotFactory.getBot(PositionState.WHITE, BotFactory.NEURAL_SIM_BOT);
        Board currentBoard = leafNode.getState().deepCopy();
        currentBoard.verifyIntegrity();
        Game simulation = new Game(currentBoard, black, white);
        simulation.play(false);
        return simulation.getWinner();
    }

    @Override
    public void run() {
        PositionState winner = simulate();
        Node curNode = leafNode;
        boolean won = winner == color;
        while (curNode != null) {
            if (won) curNode.logWin();
            else curNode.logLoss();
            won = !won;
            curNode = curNode.getParent();
        }
    }

    static class AlphaDefaultPolicyFactory implements DefaultPolicy.DefaultPolicyFactory {
        @Override
        public DefaultPolicy getDefaultPolicy(Node n, PositionState color) {
            return new AlphaDefaultPolicy(n, color);
        }
    }
}
