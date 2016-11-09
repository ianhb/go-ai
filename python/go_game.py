import math

import tensorflow as tf

NUM_CLASSES = 2

BOARD_SIZE = 19
BOARD_AREA = BOARD_SIZE * BOARD_SIZE


def inference(boards, hidden1_units, hidden2_units):
    with tf.name_scope("hidden1"):
        weights = tf.Variable(
            tf.truncated_normal([BOARD_AREA, hidden1_units], stddev=1.0 / math.sqrt(float(BOARD_AREA))), name="weights")
        biases = tf.Variable(tf.zeros([hidden1_units]), name='biases')
        hidden1 = tf.nn.relu(tf.matmul(boards, weights) + biases)
    with tf.name_scope("hidden2"):
        weights = tf.Variable(
            tf.truncated_normal([hidden1_units, hidden2_units], stddev=1.0 / math.sqrt(float(hidden1_units))),
            name="weights")
        biases = tf.Variable(tf.zeros([hidden2_units]), name='biases')
        hidden2 = tf.nn.relu(tf.matmul(hidden1, weights) + biases)
    with tf.name_scope("softmax_linear"):
        weights = tf.Variable(
            tf.truncated_normal([hidden2_units, NUM_CLASSES], stddev=1.0 / math.sqrt(float(hidden2_units))),
            name="weights")
        biases = tf.Variable(tf.zeros([NUM_CLASSES]), name='biases')
        logits = tf.nn.relu(tf.matmul(hidden2, weights) + biases)
    return logits


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
