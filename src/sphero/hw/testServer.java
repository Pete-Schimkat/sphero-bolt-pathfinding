package sphero.hw;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class testServer {
    private ServerSocket server;

    public testServer() {
        try {
            int port = 11110;
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testServer server = new testServer();
        server.connection();
    }

    public void connection() {
        System.out.println("Waiting for client ...");
        try {
            Socket socket = server.accept();
            System.out.println("Client accepted: " + socket);

            DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            boolean done = false;
            while (!done) {
                try {
                    String line = dis.readUTF();
                    System.out.println(line);
                    done = line.equals("bye");
                } catch (IOException ioe) {
                    done = true;
                }
            }
            dis.close();
            socket.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }
}

