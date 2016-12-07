package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.framework.Utils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import tensorflow.serving.Model;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Client interface to connect to grpc server that evaluates board states and potential moves using
 * a neural network
 *
 * Created by ian on 11/13/16.
 */
public class NeuralNetworkClient {

    private static final Logger logger = LogManager.getFormatterLogger(NeuralNetworkClient.class);

    private static final NeuralNetworkClient instance = new NeuralNetworkClient(Constants.NEURAL_SERVER_ADDRESS,
            Constants.NEURAL_SERVER_PORT);
    private final PredictionServiceGrpc.PredictionServiceBlockingStub blockingStub;

    private NeuralNetworkClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    private NeuralNetworkClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        blockingStub = PredictionServiceGrpc.newBlockingStub(channel);
    }

    public static NeuralNetworkClient getInstance() {
        return instance;
    }

    private static void addBoard(TensorProto.Builder builder, PositionState[][] board, PositionState color) {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int column = 0; column < Constants.BOARD_SIZE; column++) {
                PositionState indexState = board[row][column];
                if (indexState == color) {
                    builder.addIntVal(1);
                } else if (indexState == Utils.getOppositeColor(color)) {
                    builder.addIntVal(-1);
                } else {
                    builder.addIntVal(0);
                }
            }
        }
    }

    private static Predict.PredictRequest makeRequest(PositionState color, Board board, List<Position> moves) {
        Predict.PredictRequest.Builder builder = Predict.PredictRequest.newBuilder();
        builder.setModelSpec(Model.ModelSpec.newBuilder().setName("fast").build());
        TensorProto.Builder tensorBuilder = TensorProto.newBuilder();
        tensorBuilder.setDtype(DataType.DT_FLOAT);
        TensorShapeProto.Builder shapeBuilder = TensorShapeProto.newBuilder();
        shapeBuilder.addDim(0, TensorShapeProto.Dim.newBuilder().setSize(moves.size()));
        shapeBuilder.addDim(1, TensorShapeProto.Dim.newBuilder().setSize(Constants.BOARD_SIZE * Constants.BOARD_SIZE));
        tensorBuilder.setTensorShape(shapeBuilder.build());
        for (Position position : moves) {
            PositionState[][] copy = board.stateCopy();
            if (position != null) {
                copy[position.getRow()][position.getColumn()] = color;
            }
            addBoard(tensorBuilder, copy, color);
        }
        builder.putInputs("boards", tensorBuilder.build());
        return builder.build();
    }

    /**
     * Gets the best position to play from a given game state
     * Uses the bigger/slower policy neural net
     *
     * @param color             color of move to play
     * @param board             current baord state
     * @param possiblePositions possible positions to play
     * @return best move according to slow policy neural net
     */
    public Position getBestPosition(PositionState color, Board board, List<Position> possiblePositions) {
        Predict.PredictRequest request = makeRequest(color, board, possiblePositions);
        logger.trace("Sending slow RPC request to server");
        try {
            Predict.PredictResponse response = blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).predict(request);
            logger.trace("Recieved response with %s", response);
            int bestIndex = getBestIndex(response);
            return possiblePositions.get(bestIndex);
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                logger.debug("Error Getting Position");
                logger.debug(e);
            }
        }
        return possiblePositions.get(0);
    }

    /**
     * Returns the value of the current game state for color
     * Returns float between 0 and 1
     * @param color color to evaluate for
     * @param board current board state
     * @return 0-1 value of current board state
     */
    public List<Float> getValues(PositionState color, Board board, List<Position> potentialMoves) {
        Predict.PredictRequest request = makeRequest(color, board, potentialMoves);
        logger.trace("Sending value RPC request to server");

        try {
            Predict.PredictResponse response = blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).predict(request);
            logger.trace("Received %s board values", response.getOutputsCount());
            List<Float> values = getValues(response);
            Float maxValue = Collections.max(values);
            for (int i = 0; i < values.size(); i++) {
                values.set(i, values.get(i) / maxValue);
            }
            return values;
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                logger.debug("Error getting values");
                logger.debug(e);
            }
        }
        List<Float> zeroValues = new ArrayList<>();
        for (int i = 0; i < potentialMoves.size(); i++) {
            zeroValues.add(0f);
        }
        return zeroValues;
    }

    private List<Float> getValues(Predict.PredictResponse response) {
        List<Float> values = new ArrayList<>();
        TensorProto results = response.getOutputsOrThrow("labels");
        for (int i = 0; i < results.getFloatValCount(); i += 2) {
            values.add(results.getFloatVal(i));
        }
        return values;
    }

    private int getBestIndex(Predict.PredictResponse response) {
        TensorProto results = response.getOutputsOrThrow("labels");
        float bestValue = Float.NEGATIVE_INFINITY;
        int bestIndex = -1;
        for (int i = 0; i < results.getFloatValCount(); i += 2) {
            if (results.getFloatVal(i) >= bestValue) {
                bestValue = results.getFloatVal(i);
                bestIndex = (i / 2);
            }
        }
        return bestIndex;
    }

}
