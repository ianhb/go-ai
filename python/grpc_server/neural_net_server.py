import os
import time

import grpc
import numpy
import tensorflow as tf
from concurrent import futures

import constants
from generated import neural_net_pb2
from neural_nets import fast
from neural_nets import slow
from neural_nets import value


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


def build_board(board):
    board_as_features = numpy.empty(shape=(1, constants.BOARD_AREA))
    for i in range(len(board)):
        if board[i] == neural_net_pb2.Board.PLAYER:
            state = 1
        elif board[i] == neural_net_pb2.Board.OPPONENT:
            state = -1
        else:
            state = 0
        board_as_features[0][i] = state
    return board_as_features


def find_best_move(logits, session):
    win_probabilities = logits.eval(session=session)
    best_index = numpy.argmax(win_probabilities, axis=0)[0]
    return best_index, win_probabilities[best_index][0]


class NeuralNetServer(neural_net_pb2.NetServiceServicer):
    def __init__(self):
        super(NeuralNetServer, self).__init__()
        if not os.path.isfile(constants.FAST_MODEL_FILE):
            raise Exception("Fast Model File {0} doesn't exist".format(constants.FAST_MODEL_FILE))
        if not os.path.isfile(constants.SLOW_MODEL_FILE):
            raise Exception("Slow Model File {0} doesn't exist".format(constants.SLOW_MODEL_FILE))

        self._slow_graph = tf.Graph()
        with self._slow_graph.as_default():
            self._slow_session = tf.Session()
            self._slow_neural_net = slow.SlowNN()
            saver = tf.train.Saver()
            saver.restore(self._slow_session, constants.SLOW_MODEL_FILE)
        self._fast_graph = tf.Graph()
        with self._fast_graph.as_default():
            self._fast_session = tf.Session()
            self._fast_neural_net = fast.FastNN()
            saver = tf.train.Saver()
            saver.restore(self._fast_session, constants.FAST_MODEL_FILE)
        self._value_graph = tf.Graph()
        with self._value_graph.as_default():
            self._value_session = tf.Session()
            self._value_neural_net = value.ValueNN()
            saver = tf.train.Saver()
            saver.restore(self._value_session, constants.VALUE_MODEL_FILE)

        print "Models Restored"

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
        print "Received RPC"
        board = build_board(request.array)
        board_value = self.find_value(board)
        response = neural_net_pb2.BoardValue()
        print "Board has value of {0}".format(board_value)
        response.value = board_value
        return response

    def find_best_fast_move(self, states):
        with self._fast_graph.as_default():
            logits = self._fast_neural_net.inference(tf.cast(states, tf.float32))
            return find_best_move(logits, self._fast_session)

    def find_best_slow_move(self, states):
        with self._slow_graph.as_default():
            logits = self._slow_neural_net.inference(tf.cast(states, tf.float32))
            return find_best_move(logits, self._slow_session)

    def find_value(self, board):
        with self._value_graph.as_default():
            logits = self._value_neural_net.inference(tf.cast(board, tf.float32))
            board_value = logits.eval(session=self._value_session)
            return float(board_value[0][0])


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
