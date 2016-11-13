import datetime
import os
import sys
import threading
import time
from multiprocessing import Value

import tensorflow as tf

import game

GAME_DIR = '../../../kgs/sgf'
OUTPUT_FILE = 'data/fuseki-{0}.tfrecords'

counter_lock = threading.Lock()


def write_file(thread_id, files, thread_game_counter):
    print "Started thread", thread_id
    writer = tf.python_io.TFRecordWriter(OUTPUT_FILE.format(thread_id))
    for game_file in files:
        try:
            game.Game(os.path.join(GAME_DIR, game_file), writer)
        except IndexError:
            print "Game File {0} has index error".format(game_file)
        with counter_lock:
            sys.stdout.write(
                '\rWrote {0} games. Current Game from {1}                    '.format(thread_game_counter.value,
                                                                                      game_file))
            thread_game_counter.value += 1
        sys.stdout.flush()
    writer.close()


game_files = os.listdir(GAME_DIR)
game_counter = Value('i', 0)

train_thread = threading.Thread(
    target=write_file, args=["TRAIN", game_files[0: 3 * len(game_files) / 5],
                             game_counter])
valid_thread = threading.Thread(
    target=write_file, args=["VALID", game_files[3 * len(game_files) / 5: 4 * len(game_files) / 5],
                             game_counter])
test_thread = threading.Thread(
    target=write_file, args=["TEST", game_files[4 * len(game_files) / 5:],
                             game_counter])
train_thread.daemon = True
valid_thread.daemon = True
test_thread.daemon = True
train_thread.start()
valid_thread.start()
test_thread.start()

print "Started at {0}".format(datetime.datetime.now().strftime("%I:%M%p on %B %d, %Y"))

while threading.active_count() > 0:
    time.sleep(0.1)
