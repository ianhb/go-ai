import os
import time

import tensorflow as tf

import constants
from neural_nets import fast
from neural_nets import slow
from neural_nets import tools

flags = tf.app.flags

FLAGS = flags.FLAGS
flags.DEFINE_float('learning_rate', constants.LEARNING_RATE, 'Initial learning rate')
flags.DEFINE_integer('hidden1', constants.FAST_HIDDEN1, 'Number of units in hidden layer 1')
flags.DEFINE_integer('hidden2', constants.FAST_HIDDEN2, 'Number of units in hidden layer 2')
flags.DEFINE_integer('batch_size', constants.BATCH_SIZE, 'Batch size')
flags.DEFINE_string('train_dir', constants.DATA_DIR, 'Directory with training data')
flags.DEFINE_string('summary_dir', constants.SUMMARY_DIR, 'Directory with summary logs')


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
    board.set_shape([constants.BOARD_AREA])
    board = tf.cast(board, tf.float32)
    label = tf.cast(features['label'], tf.int32)
    return board, label


def inputs(train, batch_size):
    filename = os.path.join(FLAGS.train_dir, constants.TRAIN_FILE if train else constants.VALIDATION_FILE)
    with tf.name_scope('input'):
        filename_queue = tf.train.string_input_producer([filename])
        board, label = read_and_decode(filename_queue)
        boards, sparse_labels = tf.train.shuffle_batch([board, label], batch_size=batch_size, num_threads=2,
                                                       capacity=1000 + 3 * batch_size, min_after_dequeue=1000)
        return boards, sparse_labels


def train_inputs():
    return inputs(True, FLAGS.batch_size)


def valid_inputs():
    return inputs(False, FLAGS.batch_size)


def build_fast_train_func():
    t_boards, t_labels = train_inputs()
    go_neural_net = fast.FastNN()
    logits = go_neural_net.inference(t_boards)
    loss = tools.loss(logits, t_labels)
    train_op = tools.training(loss, FLAGS.learning_rate)
    return go_neural_net, train_op, loss


def build_slow_train_func():
    t_boards, t_labels = train_inputs()
    go_neural_net = slow.SlowNN()
    logits = go_neural_net.inference(t_boards)
    loss = tools.loss(logits, t_labels)
    train_op = tools.training(loss, FLAGS.learning_rate)
    return go_neural_net, train_op, loss


def run_training(sess, train_op, loss, model_name):
        init_op = tf.group(tf.initialize_all_variables(), tf.initialize_local_variables())
        saver = tf.train.Saver()
        sess.run(init_op)
        if os.path.isfile(model_name):
            print "Loading Model"
            saver.restore(sess, model_name)
            print "Model Restored"
        else:
            print "Training Model"
            merged = tf.merge_all_summaries()
            train_writer = tf.train.SummaryWriter(FLAGS.summary_dir + "/train", sess.graph)
            coord = tf.train.Coordinator()
            threads = tf.train.start_queue_runners(sess=sess, coord=coord)
            step = 0
            try:
                start_time = time.time()
                while not coord.should_stop():
                    _, loss_value, summary = sess.run([train_op, loss, merged])
                    train_writer.add_summary(summary, step)
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
            save_path = saver.save(sess, model_name)
            print "Model saved in file {0}".format(save_path)


def run_eval(sess, neural_net):
    print "Evaluating"
    v_board, v_label = valid_inputs()
    logits = neural_net.inference(v_board)
    correct_prediction = tf.equal(tf.argmax(logits, 1), tf.cast(v_label, tf.int64))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
    coord = tf.train.Coordinator()
    threads = tf.train.start_queue_runners(sess=sess, coord=coord)
    tf.scalar_summary('accuracy', accuracy)
    tf.initialize_all_variables().run()
    mean_accuracy = 0
    step = 0
    try:
        while not coord.should_stop():
            batch_accuracy = sess.run(accuracy)
            mean_accuracy += batch_accuracy
            step += 1
            if step % 100 == 0:
                print "Step {0} has cumulative accuracy: {1}".format(step, mean_accuracy / float(step))
    except tf.errors.OutOfRangeError:
        print "Out of Range"
    finally:
        coord.request_stop()
    coord.join(threads)
    print "Final Accuracy: {0}".format(mean_accuracy / float(step))


def main(_):
    with tf.Graph().as_default():
        sess = tf.Session()
        print "Training and Evaluating Fast Neural Net"
        fast_nn, fast_train, fast_loss = build_fast_train_func()
        run_training(sess, fast_train, fast_loss, constants.FAST_MODEL_FILE)
        run_eval(sess, fast_nn)
        print "Training and Evaluating Slow Neural Net"
        slow_nn, slow_train, slow_loss = build_slow_train_func()
        run_training(sess, slow_train, slow_loss, constants.SLOW_MODEL_FILE)
        run_eval(sess, slow_nn)


if __name__ == '__main__':
    tf.app.run()
