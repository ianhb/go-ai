package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Game;
import ianhblakley.goai.framework.PositionState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client to communicate with a game logging server
 * Client transforms games into states and winner
 * Server logs states and winner in Tensor format
 * <p>
 * Created by ian on 11/26/16.
 */
public class GameLoggerClient {

    private static final Logger logger = LogManager.getFormatterLogger(GameLoggerClient.class);

    private static final GameLoggerClient ourInstance = new GameLoggerClient(Constants.LOGGER_SERVER_ADDRESS, Constants.LOGGER_SERVER_PORT);
    private final GameLoggerServiceGrpc.GameLoggerServiceBlockingStub blockingStub;


    private GameLoggerClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    private GameLoggerClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        blockingStub = GameLoggerServiceGrpc.newBlockingStub(channel);
    }

    public static GameLoggerClient getInstance() {
        return ourInstance;
    }

    public void logGame(Game game) {
        GameLogger.Game.Builder gameBuilder = GameLogger.Game.newBuilder();
        switch (game.getWinner()) {
            case BLACK:
                gameBuilder.setWinner(GameLogger.Game.BoardState.BLACK);
                break;
            case WHITE:
                gameBuilder.setWinner(GameLogger.Game.BoardState.WHITE);
                break;
            default:
                gameBuilder.setWinner(GameLogger.Game.BoardState.DRAW);
                break;
        }
        GameLogger.Game.BoardState[][] currentState = new GameLogger.Game.BoardState[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (PositionState[][] state : game.getBoardStates()) {
            gameBuilder.addGameStates(stateFromBoard(state));
        }
        GameLogger.LogResponse response = blockingStub.logGame(gameBuilder.build());
        if (!response.getSuccess()) {
            logger.error("Failed to log game %s", gameBuilder.build());
        }
    }

    private GameLogger.Game.GameState stateFromBoard(PositionState[][] gameState) {
        GameLogger.Game.GameState.Builder stateBuilder = GameLogger.Game.GameState.newBuilder();
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int column = 0; column < Constants.BOARD_SIZE; column++) {
                switch (gameState[row][column]) {
                    case BLACK:
                        stateBuilder.addState(GameLogger.Game.BoardState.BLACK);
                        break;
                    case WHITE:
                        stateBuilder.addState(GameLogger.Game.BoardState.WHITE);
                        break;
                    default:
                        stateBuilder.addState(GameLogger.Game.BoardState.EMPTY);
                        break;
                }
            }
        }
        return stateBuilder.build();
    }
}
