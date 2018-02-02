import socket
import sys

class Client:
    def __init__(self, client_socket, client_address):
        self.client_socket = client_socket
        self.client_address = client_address



class Server:
    def __init__(self, ip, port):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server.setblocking(0)
        server_address = ('localhost', 10000)
        print >>sys.stderr, 'Starting up on %s port %s' % server_address
        self.server.bind(server_address)
        self.clients = []

    def __del__(self):
        self.server.close()

    def send_signal(self,msg): #msg - s means smoke, m means move
        for client in enumerate(self.clients):
            sent = client.send(msg)
            if sent == 0:
                raise RuntimeError("Socket %s connection broken" % client.client_address)


    def wait_for_client(self):
        self.server.listen(5);
        client_socket, client_address = self.server.accept()
        client = Client(client_socket, client_address)
        self.clients.append(client)