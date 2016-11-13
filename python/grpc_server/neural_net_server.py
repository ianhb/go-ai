import os
import time

import numpy
import tensorflow as tf
from concurrent import futures

import constants
import go_nn
import grpc
from grpc_server import goai_pb2


def build_moves(board, moves):
    board_as_features = numpy.empty(shape=(len(moves), constants.BOARD_AREA))
    for i in range(len(board)):
        if board[i] == goai_pb2.Board.PLAYER:
            state = 1
        elif board[i] == goai_pb2.Board.OPPONENT:
            state = -1
        else:
            state = 0
        for j in range(len(moves)):
            board_as_features[j][i] = state

    for i in range(len(moves)):
        move = moves[i]
        index = ((move.row * constants.BOARD_SIZE) + move.column)
        board_as_features[i][index] = 1
    return board_as_features


class NeuralNetServer(goai_pb2.NNBotServicer):
    def __init__(self):
        super(NeuralNetServer, self).__init__()
        if not os.path.isfile(constants.MODEL_FILE):
            raise Exception("Model File {0} doesn't exist".format(constants.MODEL_FILE))
        self._graph = tf.Graph()
        with self._graph.as_default():
            self._session = tf.Session()
            self._neural_net = go_nn.GoNN(constants.HIDDEN1, constants.HIDDEN2)
            saver = tf.train.Saver()
            saver.restore(self._session, constants.MODEL_FILE)
        print "Model Restored"

    def GetMove(self, request, context):
        print "Received RPC"
        board_list = build_moves(request.board.array, request.potential_moves)
        best_index, probability = self.find_best_move(board_list)
        print "Move {0} has win chance of {1}".format(request.potential_moves[best_index], probability)
        response = goai_pb2.MoveResponse()
        response.id = request.id
        response.best_move.row = request.potential_moves[best_index].row
        response.best_move.column = request.potential_moves[best_index].column
        response.win_probability = float(probability)
        return response

    def find_best_move(self, states):
        with self._graph.as_default():
            logits = self._neural_net.inference(tf.cast(states, tf.float32))
        win_probabilities = logits.eval(session=self._session)
        best_index = numpy.argmax(win_probabilities, axis=0)[0]
        return best_index, win_probabilities[best_index][0]


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    goai_pb2.add_NNBotServicer_to_server(NeuralNetServer(), server)
    server.add_insecure_port('[::]:' + constants.SERVER_PORT)
    server.start()
    print "Server Started"
    while True:
        time.sleep(0.1)


if __name__ == '__main__':
    serve()
