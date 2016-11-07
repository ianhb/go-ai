import numpy
import tensorflow as tf


def convert_coordinate_to_index(coordinate, board_size):
    assert len(coordinate) == 2
    return ((ord(coordinate[0]) - 97) * board_size) + (ord(coordinate[1]) - 97)


def _int64_feature(value):
    return tf.train.Feature(int64_list=tf.train.Int64List(value=[value]))


def _bytes_feature(value):
    return tf.train.Feature(bytes_list=tf.train.BytesList(value=[value]))


def _board_feature(board):
    board_feature = tf.train.Feature()
    board_feature.int64_list.value.extend(board)
    return board_feature


class Game:
    # Convention: Matrix position (i, j) stored in (i*size) + j

    def __init__(self, game_file_name, writer):
        self.writer = writer
        kgs_file = open(game_file_name, 'r')
        self.game_content = kgs_file.read()
        self.board_size = self.get_board_size()
        self.winner = self.get_winner()
        self.current_board = self.init_board()
        self.turn_number = 1
        game_position = self.game_content.find(';', 2)
        while self.game_content.find(';', game_position + 1) != -1:
            self.process_move(game_position + 1)
            game_position += 5
            self.turn_number += 1
        kgs_file.close()

    def get_board_size(self):
        index = self.game_content.find('SZ[')
        try:
            return int(self.game_content[index + 3:index + 5])
        except ValueError:
            return int(self.game_content[index + 3])

    def get_winner(self):
        index = self.game_content.find('RE[')
        if self.game_content[index + 3] == 'B':
            return 1
        elif self.game_content[index + 3] == 'W':
            return -1
        return 0

    def init_board(self):
        board = numpy.zeros(pow(self.board_size, 2), dtype=numpy.int)
        if 'HA' not in self.game_content:
            return board
        handicap_amount = int(self.game_content[self.game_content.find('HA[') + 3])
        advantage_start_index = self.game_content.find('AB[')
        for i in range(handicap_amount):
            move_start = self.game_content.find('[', advantage_start_index)
            move = self.game_content[move_start + 1:move_start + 3]
            board[convert_coordinate_to_index(move, self.board_size)] = 1
            advantage_start_index = move_start + 1
        return board

    def process_move(self, move_index):
        color = self.game_content[move_index]
        position = convert_coordinate_to_index(self.game_content[move_index + 2:move_index + 4], self.board_size)
        if color == 'B':
            self.current_board[position] = 1
        elif color == 'W':
            self.current_board[position] = -1
        example = tf.train.Example(features=tf.train.Features(feature={
            'board': _board_feature(self.current_board),
            'label': _int64_feature(self.winner),
            'turns': _int64_feature(self.turn_number)}))
        self.writer.write(example.SerializeToString())
