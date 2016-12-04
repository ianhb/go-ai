import os
import time

import tensorflow as tf
from tensorflow.contrib.session_bundle import exporter

import constants
from neural_nets import fast
from neural_nets import slow
from neural_nets import tools
from neural_nets import value

flags = tf.app.flags

FLAGS = flags.FLAGS
flags.DEFINE_float('learning_rate', constants.LEARNING_RATE, 'Initial learning rate')
flags.DEFINE_integer('hidden1', constants.FAST_HIDDEN1, 'Number of units in hidden layer 1')
flags.DEFINE_integer('hidden2', constants.FAST_HIDDEN2, 'Number of units in hidden layer 2')
flags.DEFINE_integer('batch_size', constants.BATCH_SIZE, 'Batch size')
flags.DEFINE_string('train_dir', constants.DATA_DIR, 'Directory with training data')
flags.DEFINE_string('summary_dir', constants.SUMMARY_DIR, 'Directory with summary logs')
flags.DEFINE_string('model_type', 'fast', 'Model type to train: fast, slow, value')
flags.DEFINE_integer('num_epochs', constants.NUM_EPOCHS, 'Number of times to run through training data')
flags.DEFINE_bool('log_summaries', False, 'Save summary logs')
flags.DEFINE_integer('export_version', 1, 'Model Version Number')


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


def train_inputs():
    batch_size = FLAGS.batch_size
    filename = os.path.join(FLAGS.train_dir, constants.TRAIN_FILE)
    with tf.name_scope('input'):
        filename_queue = tf.train.string_input_producer([filename])
        board, label = read_and_decode(filename_queue)
        boards, sparse_labels = tf.train.shuffle_batch([board, label], batch_size=batch_size, num_threads=2,
                                                       capacity=1000 + 3 * batch_size, min_after_dequeue=1000)
        return boards, sparse_labels


def valid_inputs():
    batch_size = FLAGS.batch_size
    filename = os.path.join(FLAGS.train_dir, constants.VALIDATION_FILE)
    with tf.name_scope('input'):
        filename_queue = tf.train.string_input_producer([filename])
        board, label = read_and_decode(filename_queue)
        boards, sparse_labels = tf.train.shuffle_batch([board, label], batch_size=batch_size, num_threads=2,
                                                       capacity=1000 + 3 * batch_size, min_after_dequeue=1000)
        return boards, sparse_labels


def build_fast_train_func():
    t_boards, t_labels = train_inputs()
    go_neural_net = fast.FastNN()
    logits = go_neural_net.inference(t_boards)
    loss = tools.soft_max_loss(logits, t_labels)
    train_op = tools.training(loss, FLAGS.learning_rate)
    return t_boards, logits, go_neural_net, train_op, loss


def build_slow_train_func():
    t_boards, t_labels = train_inputs()
    go_neural_net = slow.SlowNN()
    logits = go_neural_net.inference(t_boards)
    loss = tools.soft_max_loss(logits, t_labels)
    train_op = tools.training(loss, FLAGS.learning_rate)
    return go_neural_net, train_op, loss


def build_value_train_func():
    t_boards, t_labels = train_inputs()
    go_neural_net = value.ValueNN()
    logits = go_neural_net.inference(t_boards)
    loss = tools.soft_max_loss(logits, t_labels)
    train_op = tools.training(loss, FLAGS.learning_rate)
    return go_neural_net, train_op, loss


def run_training(sess, train_op, loss, name):
    init_op = tf.group(tf.initialize_all_variables(), tf.initialize_local_variables())
    sess.run(init_op)
    print "Training Model"
    merged = tf.merge_all_summaries()
    train_writer = tf.train.SummaryWriter(FLAGS.summary_dir + "/train/" + name, sess.graph)
    coord = tf.train.Coordinator()
    threads = tf.train.start_queue_runners(sess=sess, coord=coord)
    step = 0
    saver = tf.train.Saver()
    try:
        start_time = time.time()
        while not coord.should_stop():
            _, loss_value, summary = sess.run([train_op, loss, merged])
            if FLAGS.log_summaries:
                train_writer.add_summary(summary, step)
            if step % FLAGS.batch_size == 0:
                duration = time.time() - start_time
                start_time = time.time()
                print "Step {0}: loss = {1} ({2} sec)".format(step, loss_value, duration)
            if step % 10000 == 0:
                if name == constants.SLOW:
                    saver.save(sess, constants.SLOW_MODEL_FILE)
                elif name == constants.FAST:
                    saver.save(sess, constants.FAST_MODEL_FILE)
                elif name == constants.VALUE:
                    saver.save(sess, constants.VALUE_MODEL_FILE)
                print "Saved Checkpoint"
            step += 1
    except tf.errors.OutOfRangeError:
        print "Done training for {0} epochs, {1} steps".format(FLAGS.num_epochs, step)
    finally:
        coord.request_stop()
    coord.join(threads)
    if name == constants.SLOW:
        saver.save(sess, constants.SLOW_MODEL_FILE)
    elif name == constants.FAST:
        saver.save(sess, constants.FAST_MODEL_FILE)


def run_eval(sess, neural_net):
    print "Evaluating"
    sess.run(tf.initialize_all_variables())
    v_board, v_label = valid_inputs()
    logits = neural_net.inference(v_board)
    correct_prediction = tf.equal(tf.argmax(logits, 1), tf.cast(v_label, tf.int64))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
    coord = tf.train.Coordinator()
    threads = tf.train.start_queue_runners(sess=sess, coord=coord)
    tf.scalar_summary('accuracy', accuracy)
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


def train_fast():
    if not os.path.exists(os.path.dirname(constants.FAST_MODEL_FILE)):
        os.mkdir(os.path.dirname(constants.FAST_MODEL_FILE))
    with tf.Graph().as_default():
        sess = tf.Session()
        boards, logits, fast_nn, fast_train, fast_loss = build_fast_train_func()
        if os.path.isfile(constants.FAST_MODEL_FILE):
            saver = tf.train.Saver()
            print "Loading Fast Neural Net Model"
            saver.restore(sess, constants.FAST_MODEL_FILE)
        else:
            print "Training Fast Neural Net"
            run_training(sess, fast_train, fast_loss, constants.FAST)
        print "Evaluating Fast Neural Net"
        #run_eval(sess, fast_nn)
        print "Exporting Model with number {0}".format(FLAGS.export_version)
        saver = tf.train.Saver(sharded=True)
        model_exporter = exporter.Exporter(saver)
        model_exporter.init(
            sess.graph.as_graph_def(),
            named_graph_signatures={
                'inputs': exporter.generic_signature({'boards': boards}),
                'outputs': exporter.generic_signature({'labels': logits})
            }
        )
        model_exporter.export(constants.EXPORT_PATH, tf.constant(FLAGS.export_version), sess)
        sess.close()


def train_slow():
    if not os.path.exists(os.path.dirname(constants.SLOW_MODEL_FILE)):
        os.mkdir(os.path.dirname(constants.SLOW_MODEL_FILE))
    with tf.Graph().as_default():
        sess = tf.Session()
        slow_nn, slow_train, slow_loss = build_slow_train_func()
        if os.path.isfile(constants.SLOW_MODEL_FILE):
            saver = tf.train.Saver()
            print "Loading Slow Neural Net Model"
            saver.restore(sess, constants.SLOW_MODEL_FILE)
        else:
            print "Training Slow Neural Net"
            run_training(sess, slow_train, slow_loss, constants.SLOW)
        print "Evaluating Slow Neural Net"
        run_eval(sess, slow_nn)
        sess.close()


def train_value():
    with tf.Graph().as_default():
        sess = tf.Session()
        value_nn, value_train, value_loss = build_value_train_func()
        saver = tf.train.Saver()
        if os.path.isfile(constants.VALUE_MODEL_FILE):
            print "Loading Models"
            saver.restore(sess, constants.VALUE_MODEL_FILE)
        else:
            print "Training Value Neural Net"
            run_training(sess, value_train, value_loss, constants.VALUE)
        print "Evaluating Fast Neural Net"
        run_eval(sess, value_nn)
        sess.close()


def main(_):
    if FLAGS.model_type == constants.FAST:
        train_fast()
    elif FLAGS.model_type == constants.SLOW:
        train_slow()
    elif FLAGS.model_type == constants.VALUE:
        train_value()


if __name__ == '__main__':
    tf.app.run()
