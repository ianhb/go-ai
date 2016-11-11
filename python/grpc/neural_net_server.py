import time

from concurrent import futures

import grpc
from grpc import goai_pb2


class NeuralNetServer(goai_pb2.NNBotServicer):
    def __init__(self):
        super(NeuralNetServer, self).__init__()

    def GetMove(self, request, context):
        print "Received RPC"
        return goai_pb2.MoveResponse(id=request.id, best_move=goai_pb2.Move(row=1, column=1))


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    goai_pb2.add_NNBotServicer_to_server(NeuralNetServer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print "Server Started"
    while True:
        time.sleep(0.1)


if __name__ == '__main__':
    serve()
