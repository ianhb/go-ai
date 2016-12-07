package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensorflow.framework.TensorProto;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Client interface to connect to grpc server that evaluates board states and potential moves using
 * a neural network.
 * Only used for move requests.
 * <p>
 * Created by ian on 12/7/16.
 */
public class NeuralMoveClient {

    private static final Logger logger = LogManager.getFormatterLogger(NeuralMoveClient.class);

    private static final NeuralMoveClient ourInstance = new NeuralMoveClient(Constants.NEURAL_SERVER_ADDRESS, Constants.MOVE_PORT);

    private final PredictionServiceGrpc.PredictionServiceBlockingStub blockingStub;

    private NeuralMoveClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port));
    }

    private NeuralMoveClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        blockingStub = PredictionServiceGrpc.newBlockingStub(channel);
    }

    public static NeuralMoveClient getInstance() {
        return ourInstance;
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
        Predict.PredictRequest request = NeuralUtils.makeRequest(color, board, possiblePositions);
        logger.trace("Sending slow RPC request to server");
        try {
            Predict.PredictResponse response = blockingStub.withDeadlineAfter(5, TimeUnit.MILLISECONDS).predict(request);
            logger.trace("Recieved response with %s", response);
            int bestIndex = getBestIndex(response);
            return possiblePositions.get(bestIndex);
        } catch (StatusRuntimeException ignored) {
        }
        return possiblePositions.get(0);
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
