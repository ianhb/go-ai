package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.*;
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

    private static NeuralNetworkClient ourInstance = new NeuralNetworkClient(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
    private final NNBotGrpc.NNBotBlockingStub blockingStub;
    private final AtomicInteger idCount = new AtomicInteger(0);

    private NeuralNetworkClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    private NeuralNetworkClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        blockingStub = NNBotGrpc.newBlockingStub(channel);
    }

    public static NeuralNetworkClient getInstance() {
        return ourInstance;
    }

    public Position getBestPosition(PositionState color, int turnCount, Board board, Set<Position> possiblePositions) {
        Goai.MoveRequest request = buildRequest(color, turnCount, board, possiblePositions);
        logger.trace("Sending slow RPC request %s to server", request.getId());
        Goai.MoveResponse response = blockingStub.getMoveSlow(request);
        logger.trace("Recieved response with %s", response);
        assert request.getId() == response.getId();
        if (response.hasBestMove()) {
            return new Position(response.getBestMove().getRow(), response.getBestMove().getColumn());
        } else {
            return null;
        }
    }

    public Position getSimulationPosition(PositionState color, int turnCount, Board board, Set<Position> possiblePositions) {
        Goai.MoveRequest request = buildRequest(color, turnCount, board, possiblePositions);
        logger.trace("Sending fast RPC request %s to server", request.getId());
        Goai.MoveResponse response = blockingStub.getMoveFast(request);
        logger.trace("Recieved response with %s", response);
        assert request.getId() == response.getId();
        if (response.hasBestMove()) {
            return new Position(response.getBestMove().getRow(), response.getBestMove().getColumn());
        } else {
            return null;
        }
    }

    public float getValue(PositionState color, Board board) {
        Goai.Board board1 = buildBoard(color, board);
        logger.trace("Sending value RPC request to server");
        Goai.BoardValue value = blockingStub.getValue(board1);
        logger.trace("Received board value %s", value.getValue());
        return value.getValue();
    }

    public void logGame(Game game) {
        // TODO
    }

    private Goai.MoveRequest buildRequest(PositionState color, int turnCount, Board board, Set<Position> possiblePositions) {
        Goai.MoveRequest.Builder requestBuilder = Goai.MoveRequest.newBuilder();
        requestBuilder.setId(idCount.getAndIncrement());

        Goai.Board protoBoard = buildBoard(color, board);
        requestBuilder.setBoard(protoBoard);
        for (Position position : possiblePositions) {
            requestBuilder.addPotentialMoves(Goai.Move.newBuilder().setRow(position.getRow()).
                    setColumn(position.getColumn()).build());
        }
        requestBuilder.setTurnCount(turnCount);
        return requestBuilder.build();
    }

    private Goai.Board buildBoard(PositionState color, Board board) {
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
        return protoBoard.build();
    }

}
