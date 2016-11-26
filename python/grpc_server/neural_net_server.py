import os
import time

import grpc
import numpy
import tensorflow as tf
from concurrent import futures

import constants
from grpc_server import neural_net_pb2
from neural_nets import fast
from neural_nets import slow


def build_moves(board, moves):
    board_as_features = numpy.empty(shape=(len(moves) + 1, constants.BOARD_AREA))
    for i in range(len(board)):
        if board[i] == neural_net_pb2.Board.PLAYER:
            state = 1
        elif board[i] == neural_net_pb2.Board.OPPONENT:
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


class NeuralNetServer(neural_net_pb2.NetServiceServicer):
    def __init__(self):
        super(NeuralNetServer, self).__init__()
        if not os.path.isfile(constants.FAST_MODEL_FILE):
            raise Exception("Fast Model File {0} doesn't exist".format(constants.FAST_MODEL_FILE))
        if not os.path.isfile(constants.SLOW_MODEL_FILE):
            raise Exception("Slow Model File {0} doesn't exist".format(constants.SLOW_MODEL_FILE))

        self._graph = tf.Graph()
        with self._graph.as_default():
            self._session = tf.Session()
            self._fast_neural_net = fast.FastNN()
            self._slow_neural_net = slow.SlowNN()
            saver = tf.train.Saver()
            saver.restore(self._session, constants.FAST_MODEL_FILE)
            saver.restore(self._session, constants.SLOW_MODEL_FILE)
        print "Model Restored"

    def GetMoveFast(self, request, context):
        print "Received RPC"
        board_list = build_moves(request.board.array, request.potential_moves)
        best_index, probability = self.find_best_fast_move(board_list)
        response = neural_net_pb2.MoveResponse()
        response.id = request.id
        if best_index == len(board_list) - 1:
            print "Pass has win chance of {0}".format(probability)
        else:
            print "Move {0} has win chance of {1}".format(request.potential_moves[best_index], probability)
            response.best_move.row = request.potential_moves[best_index].row
            response.best_move.column = request.potential_moves[best_index].column
        response.win_probability = float(probability)
        return response

    def GetMoveSlow(self, request, context):
        print "Received RPC"
        board_list = build_moves(request.board.array, request.potential_moves)
        best_index, probability = self.find_best_slow_move(board_list)
        response = neural_net_pb2.MoveResponse()
        response.id = request.id
        if best_index == len(board_list) - 1:
            print "Pass has win chance of {0}".format(probability)
        else:
            print "Move {0} has win chance of {1}".format(request.potential_moves[best_index], probability)
            response.best_move.row = request.potential_moves[best_index].row
            response.best_move.column = request.potential_moves[best_index].column
        response.win_probability = float(probability)
        return response

    def GetValue(self, request, context):
        pass

    def find_best_fast_move(self, states):
        with self._graph.as_default():
            logits = self._fast_neural_net.inference(tf.cast(states, tf.float32))
            return self.find_best_move(logits)

    def find_best_slow_move(self, states):
        with self._graph.as_default():
            logits = self._slow_neural_net.inference(tf.cast(states, tf.float32))
            return self.find_best_move(logits)

    def find_best_move(self, logits):
        win_probabilities = logits.eval(session=self._session)
        best_index = numpy.argmax(win_probabilities, axis=0)[0]
        return best_index, win_probabilities[best_index][0]


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    neural_net_pb2.add_NetServiceServicer_to_server(NeuralNetServer(), server)
    server.add_insecure_port('[::]:' + constants.NET_SERVER_PORT)
    server.start()
    print "Server Started"
    while True:
        time.sleep(1)


if __name__ == '__main__':
    serve()
