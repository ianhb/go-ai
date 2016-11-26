package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Game;
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

    private static GameLoggerClient ourInstance = new GameLoggerClient(Constants.LOGGER_SERVER_ADDRESS, Constants.LOGGER_SERVER_PORT);
    private final NetServiceGrpc.NetServiceBlockingStub blockingStub;


    private GameLoggerClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    private GameLoggerClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        blockingStub = NetServiceGrpc.newBlockingStub(channel);
    }

    public static GameLoggerClient getInstance() {
        return ourInstance;
    }

    public void logGame(Game game) {

    }
}
