import threading
import time

import constants
from grpc_server import game_logger_server
from grpc_server import neural_net_server

if __name__ == '__main__':
    gls_1_thread = threading.Thread(target=neural_net_server.serve, args=(str(constants.NET_SERVER_PORT),))
    gls_2_thread = threading.Thread(target=neural_net_server.serve, args=(str(constants.NET_SERVER_PORT+1),))
    gls_3_thread = threading.Thread(target=neural_net_server.serve, args=(str(constants.NET_SERVER_PORT+2),))

    nns_thread = threading.Thread(target=game_logger_server.serve)
    gls_1_thread.daemon = True
    gls_2_thread.daemon = True
    gls_3_thread.daemon = True

    nns_thread.daemon = True
    gls_1_thread.start()
    gls_2_thread.start()
    gls_3_thread.start()
    nns_thread.start()
    while True:
        time.sleep(1)
