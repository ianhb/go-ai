import constants
import grpc
from grpc_server import goai_pb2
from grpc_server.goai_pb2 import Move


def test_connection():
    channel = grpc.insecure_channel('localhost:' + constants.SERVER_PORT)
    stub = goai_pb2.NNBotStub(channel)

    request = goai_pb2.MoveRequest()
    request.id = 1
    for i in range(361):
        request.board.array.append(goai_pb2.Board.EMPTY)
    request.turn_count = 2
    request.potential_moves.extend([Move(row=2, column=2), Move(row=1, column=1)])
    print stub.GetMove(request)


if __name__ == '__main__':
    test_connection()
