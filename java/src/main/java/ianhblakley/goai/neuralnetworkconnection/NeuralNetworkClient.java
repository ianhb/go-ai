package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.framework.Utils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Client interface to connect to grpc server that evaluates board states and potential moves using
 * a neural network
 *
 * Created by ian on 11/13/16.
 */
public class NeuralNetworkClient {

    private static final Logger logger = LogManager.getFormatterLogger(NeuralNetworkClient.class);

    private final ManagedChannel channel;
    private final NNBotGrpc.NNBotBlockingStub blockingStub;
    private final AtomicInteger idCount = new AtomicInteger(0);

    public NeuralNetworkClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    private NeuralNetworkClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = NNBotGrpc.newBlockingStub(channel);
    }

    public Position getBestPosition(PositionState color, int turnCount, Board board, Set<Position> possiblePositions) {
        Goai.MoveRequest.Builder requestBuilder = Goai.MoveRequest.newBuilder();
        requestBuilder.setId(idCount.getAndIncrement());

        Goai.Board.Builder protoBoard = Goai.Board.newBuilder();
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int column = 0; column < Constants.BOARD_SIZE; column++) {
                PositionState indexState = board.getPositionState(row, column);
                if (indexState == color) {
                    protoBoard.addArray(Goai.Board.PositionState.PLAYER);
                } else if (indexState == Utils.getOppositeColor(color)) {
                    protoBoard.addArray(Goai.Board.PositionState.OPPONENT);
                } else {
                    protoBoard.addArray(Goai.Board.PositionState.EMPTY);
                }
            }
        }
        requestBuilder.setBoard(protoBoard.build());
        for (Position position : possiblePositions) {
            requestBuilder.addPotentialMoves(Goai.Move.newBuilder().setRow(position.getRow()).
                    setColumn(position.getColumn()).build());
        }
        requestBuilder.setTurnCount(turnCount);
        Goai.MoveRequest request = requestBuilder.build();
        logger.trace("Sending RPC request %s to server", request.getId());
        Goai.MoveResponse response = blockingStub.getMove(request);
        logger.trace("Recieved response with %s", response);
        assert request.getId() == response.getId();
        return new Position(response.getBestMove().getRow(), response.getBestMove().getColumn());
    }

}
