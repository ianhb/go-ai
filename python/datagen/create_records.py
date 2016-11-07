import os
import sys
import threading
import time
from multiprocessing import Value

import tensorflow as tf

import game

GAME_DIR = '../../../kgs/sgf'
OUTPUT_FILE = 'data/fuseki-{0}-{1}.tfrecords'
GAMES_PER_FILE = 1000
THREADS = 4

tf.app.flags.DEFINE_integer("validation_size", 10000,
                            "Number of boards to separate from training data for validation set")
FLAGS = tf.app.flags.FLAGS

counter_lock = threading.Lock()


def write_file(thread_id, files, thread_game_counter):
    print "Started thread", thread_id
    file_count = 0
    game_count = 0
    writer = tf.python_io.TFRecordWriter(OUTPUT_FILE.format(thread_id, file_count))
    for game_file in files:
        if game_count == GAMES_PER_FILE:
            writer.close()
            file_count += 1
            game_count = 0
            writer = tf.python_io.TFRecordWriter(OUTPUT_FILE.format(thread_id, file_count))
            print "Creating new file {0}".format(OUTPUT_FILE.format(thread_id, file_count))
        game.Game(os.path.join(GAME_DIR, game_file), writer)
        with counter_lock:
            sys.stdout.write(
                '\rWrote {0} games. Current Game from {1}                    '.format(thread_game_counter.value,
                                                                                      game_file))
            thread_game_counter.value += 1
        sys.stdout.flush()
        game_count += 1
    writer.close()


threads = []
game_files = os.listdir(GAME_DIR)
game_counter = Value('i', 0)
for i in range(THREADS):
    t = threading.Thread(
        target=write_file, args=[i, game_files[i * len(game_files) / THREADS: (i + 1) * len(game_files) / THREADS],
                                 game_counter])
    threads.append(t)
    t.daemon = True
    t.start()

while threading.active_count() > 0:
    time.sleep(0.1)
