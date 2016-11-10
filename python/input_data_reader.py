import os
import time

import tensorflow as tf

import go_nn

flags = tf.app.flags

FLAGS = flags.FLAGS
flags.DEFINE_float('learning_rate', 0.01, 'Initial learning rate')
flags.DEFINE_integer('num_epochs', 2, 'Number of epochs to run trainer')
flags.DEFINE_integer('hidden1', 128, 'Number of units in hidden layer 1')
flags.DEFINE_integer('hidden2', 32, 'Number of units in hidden layer 2')
flags.DEFINE_integer('batch_size', 1000, 'Batch size')
flags.DEFINE_string('train_dir', 'datagen/data', 'Directory with training data')

TRAIN_FILE = 'fuseki-TRAIN.tfrecords'
VALIDATION_FILE = 'fuseki.VALID.tfrecords'


def read_and_decode(filename_queue):
    reader = tf.TFRecordReader()
    _, serialized_example = reader.read(filename_queue)
    features = tf.parse_single_example(
        serialized_example,
        features={
            'board': tf.FixedLenFeature([], tf.string),
            'label': tf.FixedLenFeature([], tf.int64)
        }
    )
    board = tf.decode_raw(features['board'], tf.int8)
    board.set_shape([go_nn.BOARD_AREA])
    board = tf.cast(board, tf.float32)
    label = tf.cast(features['label'], tf.int32)
    return board, label


def inputs(train, batch_size, num_epochs):
    if not num_epochs: num_epochs = None
    filename = os.path.join(FLAGS.train_dir, TRAIN_FILE if train else VALIDATION_FILE)
    with tf.name_scope('input'):
        filename_queue = tf.train.string_input_producer([filename], num_epochs=num_epochs)
        board, label = read_and_decode(filename_queue)
        boards, sparse_labels = tf.train.shuffle_batch([board, label], batch_size=batch_size, num_threads=2,
                                                       capacity=1000 + 3 * batch_size, min_after_dequeue=1000)
        return boards, sparse_labels


def train_inputs():
    return inputs(True, FLAGS.batch_size, FLAGS.num_epochs)


def valid_inputs():
    return inputs(False, FLAGS.batch_size, FLAGS.num_epochs)


def run_training():
    with tf.Graph().as_default():
        t_boards, t_labels = train_inputs()
        go_neural_net = go_nn.GoNN(FLAGS.hidden1, FLAGS.hidden2)
        logits = go_neural_net.inference(t_boards)
        loss = go_neural_net.loss(logits, t_labels)
        train_op = go_neural_net.training(loss, FLAGS.learning_rate)
        init_op = tf.group(tf.initialize_all_variables(), tf.initialize_local_variables())
        sess = tf.Session()
        sess.run(init_op)
        coord = tf.train.Coordinator()
        threads = tf.train.start_queue_runners(sess=sess, coord=coord)
        step = 0
        try:
            start_time = time.time()
            while not coord.should_stop():
                _, loss_value = sess.run([train_op, loss])
                if step % FLAGS.batch_size == 0:
                    duration = time.time() - start_time
                    start_time = time.time()
                    print "Step {0}: loss = {1} ({2} sec)".format(step, loss_value, duration)
                step += 1
        except tf.errors.OutOfRangeError:
            print "Done training for {0} epochs, {1} steps".format(FLAGS.num_epochs, step)
        finally:
            coord.request_stop()
        coord.join(threads)
        print "Evaluating"
        v_boards, v_labels = valid_inputs()
        correct_prediction = tf.equal(tf.argmax(go_neural_net.inference(v_boards), 1), tf.argmax(v_labels, 1))
        accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
        print sess.run(accuracy)
        sess.close()


def main(_):
    run_training()


if __name__ == '__main__':
    tf.app.run()
