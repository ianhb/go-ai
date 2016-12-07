package ianhblakley.goai.neuralnetworkconnection;

import ianhblakley.goai.Constants;
import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Position;
import ianhblakley.goai.framework.PositionState;
import ianhblakley.goai.framework.Utils;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import tensorflow.serving.Model;
import tensorflow.serving.Predict;

import java.util.List;

/**
 * Contains shared utilities for neural network clients
 * <p>
 * Created by ian on 12/7/16.
 */
class NeuralUtils {

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

    static Predict.PredictRequest makeRequest(PositionState color, Board board, List<Position> moves) {
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
}
