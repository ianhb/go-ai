import math

import tensorflow as tf

NUM_CLASSES = 2

BOARD_SIZE = 19
BOARD_AREA = BOARD_SIZE * BOARD_SIZE


def loss(logits, labels):
    labels = tf.to_int64(labels)
    cross_entropy = tf.nn.sparse_softmax_cross_entropy_with_logits(logits, labels, name='xentropy')
    loss = tf.reduce_mean(cross_entropy, name='xentropy_mean')
    return loss


def training(loss, learning_rate):
    tf.scalar_summary(loss.op.name, loss)
    optimizer = tf.train.GradientDescentOptimizer(learning_rate)
    global_step = tf.Variable(0, name="global_step", trainable=False)
    train_op = optimizer.minimize(loss, global_step=global_step)
    return train_op


def evaluation(logits, labels):
    correct = tf.nn.in_top_k(logits, labels, 1)
    return tf.reduce_sum(tf.cast(correct, tf.int32))


class GoNN:
    def __init__(self, hidden1_units, hidden2_units):
        with tf.name_scope("hidden1"):
            self._h1_weights = tf.Variable(
                tf.truncated_normal([BOARD_AREA, hidden1_units], stddev=1.0 / math.sqrt(float(BOARD_AREA))),
                name="weights")
            self._h1_biases = tf.Variable(tf.zeros([hidden1_units]), name='biases')
            variable_summaries(self._h1_weights, "hidden1/weights")
            variable_summaries(self._h1_biases, "hidden1/biases")
        with tf.name_scope("hidden2"):
            self._h2_weights = tf.Variable(
                tf.truncated_normal([hidden1_units, hidden2_units], stddev=1.0 / math.sqrt(float(hidden1_units))),
                name="weights")
            self._h2_biases = tf.Variable(tf.zeros([hidden2_units]), name='biases')
            variable_summaries(self._h2_weights, "hidden2/weights")
            variable_summaries(self._h2_biases, "hidden2/biases")
        with tf.name_scope("softmax_linear"):
            self._sm_weights = tf.Variable(
                tf.truncated_normal([hidden2_units, NUM_CLASSES], stddev=1.0 / math.sqrt(float(hidden2_units))),
                name="weights")
            self._sm_biases = tf.Variable(tf.zeros([NUM_CLASSES]), name='biases')
            variable_summaries(self._sm_weights, "sm/weights")
            variable_summaries(self._sm_biases, "sm/biases")

    def inference(self, boards):
        with tf.name_scope("hidden1"):
            hidden1 = tf.nn.relu(tf.matmul(boards, self._h1_weights) + self._h1_biases)
            tf.histogram_summary("hidden1/pre-activations", hidden1)
        with tf.name_scope("hidden2"):
            hidden2 = tf.nn.relu(tf.matmul(hidden1, self._h2_weights) + self._h2_biases)
            tf.histogram_summary("hidden2/pre-activations", hidden1)
        with tf.name_scope("softmax_linear"):
            logits = tf.nn.relu(tf.matmul(hidden2, self._sm_weights) + self._sm_biases)
            tf.histogram_summary("sm/pre-activations", hidden1)
        return logits


def variable_summaries(var, name):
    # type: (tf.Variable, string) -> None
    with tf.name_scope('summaries'):
        mean = tf.reduce_mean(var)
        tf.scalar_summary('mean/' + name, mean)
        with tf.name_scope('stddev'):
            stddev = tf.sqrt(tf.reduce_mean(tf.square(var - mean)))
        tf.scalar_summary('stddev/' + name, stddev)
        tf.scalar_summary('max/' + name, tf.reduce_max(var))
        tf.scalar_summary('min/' + name, tf.reduce_min(var))
        tf.histogram_summary(name, var)
