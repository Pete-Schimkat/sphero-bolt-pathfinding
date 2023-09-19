# Import socket module
import socket

# Create a socket object
s = socket.socket()

# Define the port on which you want to connect
port = 11110
host = socket.gethostname()
# connect to the server on local computer
s.connect((host, port))
while True:
    command = input("command: ")
    if command == "end":
        break
    s.send(command.encode('ascii'))
# close the connection
s.close()
