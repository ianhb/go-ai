import grpc

import constants
from grpc_server import neural_net_pb2
from grpc_server.neural_net_pb2 import Move


def test_connection():
    channel = grpc.insecure_channel('localhost:' + constants.NET_SERVER_PORT)
    stub = neural_net_pb2.NetServiceStub(channel)

    request = neural_net_pb2.MoveRequest()
    request.id = 1
    for i in range(361):
        request.board.array.append(neural_net_pb2.Board.EMPTY)
    request.turn_count = 2
    request.potential_moves.extend([Move(row=2, column=2), Move(row=1, column=1)])
    print stub.GetMoveSlow(request)


if __name__ == '__main__':
    test_connection()
