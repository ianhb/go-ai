import threading
import time

import constants
from grpc_server import game_logger_server
from grpc_server import neural_net_server

if __name__ == '__main__':
    game_logger_server.serve()


