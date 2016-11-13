import os
import time

import tensorflow as tf

import go_nn

flags = tf.app.flags

FLAGS = flags.FLAGS
flags.DEFINE_float('learning_rate', 0.01, 'Initial learning rate')
flags.DEFINE_integer('hidden1', 128, 'Number of units in hidden layer 1')
flags.DEFINE_integer('hidden2', 32, 'Number of units in hidden layer 2')
flags.DEFINE_integer('batch_size', 1000, 'Batch size')
flags.DEFINE_string('train_dir', 'datagen/data', 'Directory with training data')
flags.DEFINE_string('summary_dir', 'summaries', 'Directory with summary logs')

TRAIN_FILE = 'fuseki-TRAIN.tfrecords'
VALIDATION_FILE = 'fuseki-VALID.tfrecords'


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


def inputs(train, batch_size):
    filename = os.path.join(FLAGS.train_dir, TRAIN_FILE if train else VALIDATION_FILE)
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


def run_training(sess):
        t_boards, t_labels = train_inputs()
        go_neural_net = go_nn.GoNN(FLAGS.hidden1, FLAGS.hidden2)
        logits = go_neural_net.inference(t_boards)
        loss = go_nn.loss(logits, t_labels)
        train_op = go_nn.training(loss, FLAGS.learning_rate)
        init_op = tf.group(tf.initialize_all_variables(), tf.initialize_local_variables())
        saver = tf.train.Saver()
        sess.run(init_op)
        if os.path.isfile(os.path.join(FLAGS.train_dir, "model.ckpt")):
            print "Loading Model"
            saver.restore(sess, os.path.join(FLAGS.train_dir, "model.ckpt"))
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
            save_path = saver.save(sess, os.path.join(FLAGS.train_dir, "model.ckpt"))
            print "Model saved in file {0}".format(save_path)

        return go_neural_net


def run_eval(sess, go_neural_net):
    print "Evaluating"
    v_board, v_label = valid_inputs()
    logits = go_neural_net.inference(v_board)
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
        sess = tf.InteractiveSession()
        nn = run_training(sess)
        run_eval(sess, nn)


if __name__ == '__main__':
    tf.app.run()
