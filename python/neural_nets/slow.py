import math

import tensorflow as tf

import constants
from tools import variable_summaries


class SlowNN:
    def __init__(self):
        with tf.name_scope("slow"):
            with tf.name_scope("hidden1"):
                self._h1_weights = tf.Variable(
                    tf.truncated_normal([constants.BOARD_AREA, constants.SLOW_HIDDEN1],
                                        stddev=1.0 / math.sqrt(float(constants.BOARD_AREA))),
                    name="weights")
                self._h1_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN1]), name='biases')
                variable_summaries(self._h1_weights, "hidden1/weights")
                variable_summaries(self._h1_biases, "hidden1/biases")
            with tf.name_scope("hidden2"):
                self._h2_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN1, constants.SLOW_HIDDEN2],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN1))),
                    name="weights")
                self._h2_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN2]), name='biases')
                variable_summaries(self._h2_weights, "hidden2/weights")
                variable_summaries(self._h2_biases, "hidden2/biases")
            with tf.name_scope("hidden3"):
                self._h3_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN2, constants.SLOW_HIDDEN3],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN2))),
                    name="weights")
                self._h3_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN3]), name='biases')
                variable_summaries(self._h3_weights, "hidden3/weights")
                variable_summaries(self._h3_biases, "hidden3/biases")
            with tf.name_scope("hidden4"):
                self._h4_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN3, constants.SLOW_HIDDEN4],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN3))),
                    name="weights")
                self._h4_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN4]), name='biases')
                variable_summaries(self._h4_weights, "hidden4/weights")
                variable_summaries(self._h4_biases, "hidden4/biases")
            with tf.name_scope("hidden5"):
                self._h5_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN4, constants.SLOW_HIDDEN5],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN4))),
                    name="weights")
                self._h5_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN5]), name='biases')
                variable_summaries(self._h5_weights, "hidden5/weights")
                variable_summaries(self._h5_biases, "hidden5/biases")
            with tf.name_scope("hidden6"):
                self._h6_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN5, constants.SLOW_HIDDEN6],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN5))),
                    name="weights")
                self._h6_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN6]), name='biases')
                variable_summaries(self._h6_weights, "hidden6/weights")
                variable_summaries(self._h6_biases, "hidden6/biases")
            with tf.name_scope("hidden7"):
                self._h7_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN6, constants.SLOW_HIDDEN7],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN6))),
                    name="weights")
                self._h7_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN7]), name='biases')
                variable_summaries(self._h7_weights, "hidden7/weights")
                variable_summaries(self._h7_biases, "hidden7/biases")
            with tf.name_scope("hidden8"):
                self._h8_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN7, constants.SLOW_HIDDEN8],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN7))),
                    name="weights")
                self._h8_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN8]), name='biases')
                variable_summaries(self._h8_weights, "hidden8/weights")
                variable_summaries(self._h8_biases, "hidden8/biases")
            with tf.name_scope("hidden9"):
                self._h9_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN8, constants.SLOW_HIDDEN9],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN8))),
                    name="weights")
                self._h9_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN9]), name='biases')
                variable_summaries(self._h9_weights, "hidden9/weights")
                variable_summaries(self._h9_biases, "hidden9/biases")
            with tf.name_scope("hidden10"):
                self._h10_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN9, constants.SLOW_HIDDEN10],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN9))),
                    name="weights")
                self._h10_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN10]), name='biases')
                variable_summaries(self._h10_weights, "hidden10/weights")
                variable_summaries(self._h10_biases, "hidden10/biases")
            with tf.name_scope("hidden11"):
                self._h11_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN10, constants.SLOW_HIDDEN11],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN10))),
                    name="weights")
                self._h11_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN11]), name='biases')
                variable_summaries(self._h11_weights, "hidden11/weights")
                variable_summaries(self._h11_biases, "hidden11/biases")
            with tf.name_scope("hidden12"):
                self._h12_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN11, constants.SLOW_HIDDEN12],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN11))),
                    name="weights")
                self._h12_biases = tf.Variable(tf.zeros([constants.SLOW_HIDDEN12]), name='biases')
                variable_summaries(self._h12_weights, "hidden12/weights")
                variable_summaries(self._h12_biases, "hidden12/biases")
            with tf.name_scope("softmax_linear"):
                self._sm_weights = tf.Variable(
                    tf.truncated_normal([constants.SLOW_HIDDEN12, constants.NUM_CLASSES],
                                        stddev=1.0 / math.sqrt(float(constants.SLOW_HIDDEN12))),
                    name="weights")
                self._sm_biases = tf.Variable(tf.zeros([constants.NUM_CLASSES]), name='biases')
                variable_summaries(self._sm_weights, "sm/weights")
                variable_summaries(self._sm_biases, "sm/biases")

    def inference(self, boards):
        with tf.name_scope("slow"):
            with tf.name_scope("hidden1"):
                hidden1 = tf.nn.relu(tf.matmul(boards, self._h1_weights) + self._h1_biases)
                tf.histogram_summary("hidden1/pre-activations", hidden1)
            with tf.name_scope("hidden2"):
                hidden2 = tf.nn.relu(tf.matmul(hidden1, self._h2_weights) + self._h2_biases)
                tf.histogram_summary("hidden2/pre-activations", hidden1)
            with tf.name_scope("hidden3"):
                hidden3 = tf.nn.relu(tf.matmul(hidden2, self._h3_weights) + self._h3_biases)
                tf.histogram_summary("hidden3/pre-activations", hidden3)
            with tf.name_scope("hidden4"):
                hidden4 = tf.nn.relu(tf.matmul(hidden3, self._h4_weights) + self._h4_biases)
                tf.histogram_summary("hidden4/pre-activations", hidden1)
            with tf.name_scope("hidden5"):
                hidden5 = tf.nn.relu(tf.matmul(hidden4, self._h5_weights) + self._h5_biases)
                tf.histogram_summary("hidden5/pre-activations", hidden1)
            with tf.name_scope("hidden6"):
                hidden6 = tf.nn.relu(tf.matmul(hidden5, self._h6_weights) + self._h6_biases)
                tf.histogram_summary("hidden6/pre-activations", hidden1)
            with tf.name_scope("hidden7"):
                hidden7 = tf.nn.relu(tf.matmul(hidden6, self._h7_weights) + self._h7_biases)
                tf.histogram_summary("hidden7/pre-activations", hidden1)
            with tf.name_scope("hidden8"):
                hidden8 = tf.nn.relu(tf.matmul(hidden7, self._h8_weights) + self._h8_biases)
                tf.histogram_summary("hidden8/pre-activations", hidden1)
            with tf.name_scope("hidden9"):
                hidden9 = tf.nn.relu(tf.matmul(hidden8, self._h9_weights) + self._h9_biases)
                tf.histogram_summary("hidden9/pre-activations", hidden1)
            with tf.name_scope("hidden10"):
                hidden10 = tf.nn.relu(tf.matmul(hidden9, self._h10_weights) + self._h10_biases)
                tf.histogram_summary("hidden10/pre-activations", hidden1)
            with tf.name_scope("hidden11"):
                hidden11 = tf.nn.relu(tf.matmul(hidden10, self._h11_weights) + self._h11_biases)
                tf.histogram_summary("hidden11/pre-activations", hidden1)
            with tf.name_scope("hidden12"):
                hidden12 = tf.nn.relu(tf.matmul(hidden11, self._h12_weights) + self._h12_biases)
                tf.histogram_summary("hidden12/pre-activations", hidden1)
            with tf.name_scope("softmax_linear"):
                logits = tf.nn.relu(tf.matmul(hidden12, self._sm_weights) + self._sm_biases)
                tf.histogram_summary("sm/pre-activations", logits)
        return logits
