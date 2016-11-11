import grpc
from grpc import goai_pb2
from grpc.goai_pb2 import Move


def test_connection():
    channel = grpc.insecure_channel('localhost:50051')
    stub = goai_pb2.NNBotStub(channel)

    request = goai_pb2.MoveRequest()
    request.id = 1
    request.board.array.extend([goai_pb2.Board.BLACK, goai_pb2.Board.WHITE])
    request.turn_count = 2
    request.potential_moves.extend([Move(row=1, column=1), Move(row=2, column=2)])
    print stub.GetMove(request)
