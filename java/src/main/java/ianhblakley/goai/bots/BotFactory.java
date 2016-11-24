package ianhblakley.goai.bots;

import ianhblakley.goai.framework.PositionState;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to make creating bots easier
 * <p>
 * Created by ian on 11/14/16.
 */
public class BotFactory {

    public static final String NEURAL_SIM_BOT = "NeuralSimBot";
    private static final String RANDOM_BOT = "RandomBot";
    private static final String RANDOM_MCTS_BOT = "RandomMCTSBot";
    private static final String UCT_BOT = "UctBot";
    private static final String NEURAL_NET_BOT = "NeuralNetBot";
    private static final String ALPHA_GO_BOT = "AlphaGoBot";

    public static Bot getBot(PositionState color, String type) {
        switch (type) {
            case RANDOM_BOT:
                return new RandomBot(color);
            case RANDOM_MCTS_BOT:
                return new RandomMCTSBot(color);
            case UCT_BOT:
                return new UctBot(color);
            case NEURAL_NET_BOT:
                return new NeuralNetBot(color);
            case ALPHA_GO_BOT:
                return new AlphaGoBot(color);
            case NEURAL_SIM_BOT:
                return new AlphaGoBot.SimBot(color);
            default:
                return new RandomBot(color);
        }
    }

    public static List<String> botTypes() {
        List<String> botSet = new ArrayList<>();
        botSet.add(UCT_BOT);
        botSet.add(NEURAL_NET_BOT);
        botSet.add(RANDOM_MCTS_BOT);
        botSet.add(RANDOM_BOT);
        return botSet;
    }
}
