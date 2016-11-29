NUM_CLASSES = 2

BOARD_SIZE = 19
BOARD_AREA = BOARD_SIZE * BOARD_SIZE

SLOW = 'slow'
FAST = 'fast'
VALUE = 'value'

SLOW_MODEL_FILE = "models/slow_model.ckpt"
FAST_MODEL_FILE = "models/fast_model.ckpt"
VALUE_MODEL_FILE = "models/value_model.ckpt"
TRAIN_FILE = 'fuseki-TRAIN.tfrecords'
VALIDATION_FILE = 'fuseki-VALID.tfrecords'

LEARNING_RATE = 0.01
FAST_HIDDEN1 = 128
FAST_HIDDEN2 = 32
BATCH_SIZE = 1000
NUM_EPOCHS = 2

SLOW_HIDDEN1 = 256
SLOW_HIDDEN2 = 256
SLOW_HIDDEN3 = 256
SLOW_HIDDEN4 = 256
SLOW_HIDDEN5 = 128
SLOW_HIDDEN6 = 128
SLOW_HIDDEN7 = 128
SLOW_HIDDEN8 = 64
SLOW_HIDDEN9 = 64
SLOW_HIDDEN10 = 32
SLOW_HIDDEN11 = 32
SLOW_HIDDEN12 = 16


DATA_DIR = "datagen/data"
GENERATED_DATA_DIR = "datagen/generated"
SUMMARY_DIR = "summaries"

NET_SERVER_PORT = '50051'
LOG_SERVER_PORT = '50052'

LOGGED_GAMES_PER_FILE = 10000
