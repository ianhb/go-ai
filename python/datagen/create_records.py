import os
import sys

import tensorflow as tf

import game

GAME_DIR = '../../../kgs/sgf'
OUTPUT_FILE = 'fuseki.tfrecords'

tf.app.flags.DEFINE_integer("validation_size", 10000,
                            "Number of boards to separate from training data for validation set")
FLAGS = tf.app.flags.FLAGS

print "Writing to ", OUTPUT_FILE
writer = tf.python_io.TFRecordWriter(OUTPUT_FILE)
count = 0
for game_file in os.listdir(GAME_DIR):
    sys.stdout.write('\rWrote {0} games. Current Game from {1}                    '.format(count, game_file))
    sys.stdout.flush()
    game.Game(os.path.join(GAME_DIR, game_file), writer)
    count += 1
writer.close()
