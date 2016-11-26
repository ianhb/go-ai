import threading
import time

from grpc_server import game_logger_server
from grpc_server import neural_net_server

if __name__ == '__main__':
    gls_thread = threading.Thread(target=game_logger_server.serve)
    nns_thread = threading.Thread(target=neural_net_server.serve)
    gls_thread.daemon = True
    nns_thread.daemon = True
    gls_thread.start()
    nns_thread.start()
    while True:
        time.sleep(1)
