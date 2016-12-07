package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensorflow.framework.TensorProto;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Client interface to connect to grpc server that evaluates board states and potential moves using
 * a neural network.
 * Only used for value requests.
 *
 * Created by ian on 11/13/16.
 */
public class ValueClient {

    private static final Logger logger = LogManager.getFormatterLogger(ValueClient.class);

    private static final ValueClient instance = new ValueClient(Constants.NEURAL_SERVER_ADDRESS,
            Constants.VALUE_PORT);
    private final PredictionServiceGrpc.PredictionServiceBlockingStub blockingStub;

    private ValueClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    private ValueClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        blockingStub = PredictionServiceGrpc.newBlockingStub(channel);
    }

    public static ValueClient getInstance() {
        return instance;
    }

    /**
     * Returns the value of the current game state for color
     * Returns float between 0 and 1
     * @param color color to evaluate for
     * @param board current board state
     * @return 0-1 value of current board state
     */
    public List<Float> getValues(PositionState color, Board board, List<Position> potentialMoves) {
        Predict.PredictRequest request = NeuralUtils.makeRequest(color, board, potentialMoves);
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


}
