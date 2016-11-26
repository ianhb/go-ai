import datetime as dt
import os
import time

import grpc
import numpy
import tensorflow as tf
from concurrent import futures

import constants
from grpc_server import game_logger_pb2

GENERATED_FILENAME = "gen-{0}.tfrecords".format(dt.datetime.now().strftime("%Y_%m_%d_%H_%M_%S_%f"))


def _int64_feature(value):
    return tf.train.Feature(int64_list=tf.train.Int64List(value=[value]))


def _bytes_feature(value):
    return tf.train.Feature(bytes_list=tf.train.BytesList(value=[value]))


def _board_feature(board):
    return _bytes_feature(board.tostring())


class GameLoggerServer(game_logger_pb2.GameLoggerServiceServicer):
    def __init__(self):
        super(GameLoggerServer, self).__init__()
        self._game_count = 0
        self._writer = tf.python_io.TFRecordWriter(os.path.join(constants.GENERATED_DATA_DIR,
                                                                GENERATED_FILENAME))

    def LogGame(self, request, context):

        winner = request.winner
        if winner == game_logger_pb2.Game.BLACK:
            value = 1
        else:
            value = 0

        states = request.gameStates
        turn_count = 1
        for state in states:
            self.process_move(state, value, turn_count)
            turn_count += 1

        if self._game_count >= constants.LOGGED_GAMES_PER_FILE:
            self._game_count = 0
            self._writer.close()
            self._writer = tf.python_io.TFRecordWriter(os.path.join(constants.GENERATED_DATA_DIR,
                                                                    GENERATED_FILENAME))

    def process_move(self, state, winner, turn_count):
        board = numpy.zeros((361, 1))
        for index in range(state.size):
            if state[index] == game_logger_pb2.Game.BLACK:
                board[index] = 1
            elif state[index] == game_logger_pb2.Game.WHITE:
                board[index] = -1
        example = tf.train.Example(features=tf.train.Features(feature={
            'board': _board_feature(board),
            'label': _int64_feature(winner),
            'turns': _int64_feature(turn_count)}))
        self._writer.write(example.SerializeToString())


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    game_logger_pb2.add_GameLoggerServiceServicer_to_server(GameLoggerServer(), server)
    server.add_insecure_port('[::]:' + constants.LOG_SERVER_PORT)
    server.start()
    print "Game Logger Server Started"
    while True:
        time.sleep(1)
