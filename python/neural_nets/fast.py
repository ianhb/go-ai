import math

import tensorflow as tf

import constants
from neural_nets.tools import variable_summaries


class FastNN:
    def __init__(self):
        with tf.name_scope("fast"):
            with tf.name_scope("hidden1"):
                self._h1_weights = tf.Variable(
                    tf.truncated_normal([constants.BOARD_AREA, constants.FAST_HIDDEN1],
                                        stddev=1.0 / math.sqrt(float(constants.BOARD_AREA))),
                    name="weights")
                self._h1_biases = tf.Variable(tf.zeros([constants.FAST_HIDDEN1]), name='biases')

            with tf.name_scope("hidden2"):
                self._h2_weights = tf.Variable(
                    tf.truncated_normal([constants.FAST_HIDDEN1, constants.FAST_HIDDEN2],
                                        stddev=1.0 / math.sqrt(float(constants.FAST_HIDDEN1))),
                    name="weights")
                self._h2_biases = tf.Variable(tf.zeros([constants.FAST_HIDDEN2]), name='biases')

            with tf.name_scope("softmax_linear"):
                self._sm_weights = tf.Variable(
                    tf.truncated_normal([constants.FAST_HIDDEN2, constants.NUM_CLASSES],
                                        stddev=1.0 / math.sqrt(float(constants.FAST_HIDDEN2))),
                    name="weights")
                self._sm_biases = tf.Variable(tf.zeros([constants.NUM_CLASSES]), name='biases')

    def inference(self, boards):
        with tf.name_scope("fast"):
            with tf.name_scope("hidden1"):
                hidden1 = tf.nn.relu(tf.matmul(boards, self._h1_weights) + self._h1_biases)
            with tf.name_scope("hidden2"):
                hidden2 = tf.nn.relu(tf.matmul(hidden1, self._h2_weights) + self._h2_biases)
            with tf.name_scope("softmax_linear"):
                logits = tf.nn.relu(tf.matmul(hidden2, self._sm_weights) + self._sm_biases)
        return logits