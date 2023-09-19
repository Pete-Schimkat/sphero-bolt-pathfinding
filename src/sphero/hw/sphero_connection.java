package sphero.hw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

import static java.lang.Thread.sleep;


public class sphero_connection {
    private static int direction = 0; //Ausrichtung des Spheros. 0=oben, 1=rechts,2=unten,3=rechts
    static InetAddress host;
    static Socket socket;
    static DataInputStream input;
    static DataOutputStream dos;

    /**
     * Baut eine Verbindung zu unserem Server auf.
      */
    public static void connectToServer() {

        try {
            host = InetAddress.getLocalHost();
            socket = new Socket(host, 11110);
            input = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }

    }

    /**
     * Startet den Python server
     *
     * @throws InterruptedException
     */
    public static void serverStart() throws InterruptedException {
        boolean connectToServer = false;
        try {
            // run the "python server.py" command
            // using the Runtime exec method:
            Runtime.getRuntime().exec("python src/sphero/hw/server.py");
            connectToServer = true;
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
        if (connectToServer) {
            sleep(30000);
        }
    }

    /**
     * Ruf die Methode auf, die den Python-Server startet
     */
    public static void runClient() {
        connectToServer();
    }

    /**
     * berechnet den Winkel in dem sich der Sphero drehen soll
     *
     * @param x1 X-Koordinate wo der Sphero grade steht
     * @param x2 X-Koordinate wo der Sphero hin soll
     * @param y1 Y-Koordinate wo der Sphero grade steht
     * @param y2 Y-Koordinate wo der Sphero hin soll
     * @return int für den Drehwinkel
     */
    public static int calculateSpin(int x1, int x2, int y1, int y2) {
        int spin = 0;
        int targetDir;
        if (y1 > y2) {
            targetDir = 3;
        } else if (y1 < y2) {
            targetDir = 1;
        } else if (x1 < x2) {
            targetDir = 2;
        } else if (x1 > x2) {
            targetDir = 0;
        } else {
            return -1;
        }

        while (targetDir != direction) {
            spin = spin + 90;
            direction++;
            direction = direction % 4;
        }
        if (spin == 270) {
            spin = -90;
        }
        return spin;
    }

    /**
     * Ermöglicht die Steuerung des Spheros über die Knöpfe in der GUI
     *
     * @param rL true wenn nach links gefahren werden soll
     * @param rR true wenn nach recht gefahren werden soll
     * @param rB true wenn rückwarts gefahren werden soll
     */
    public static void guiMovement(boolean rL, boolean rR, boolean rB, boolean activity) {

        LinkedList<String> newCommands = new LinkedList<>();
        if (rL) {
            newCommands.add("spin");
            int spin = calculateSpin(0, 0, 1, 0);
            newCommands.add(Integer.toString(spin));
        } else if (rR) {
            newCommands.add("spin");
            int spin = calculateSpin(0, 0, 0, 1);
            newCommands.add(Integer.toString(spin));
        } else if (rB) {
            newCommands.add("spin");
            int spin = calculateSpin(0, 1, 0, 0);
            newCommands.add(Integer.toString(spin));
        } else if (activity) {
            newCommands.add("activity");
        } else {
            newCommands.add("spin");
            int spin = calculateSpin(1, 0, 0, 0);
            newCommands.add(Integer.toString(spin));
        }
        newCommands.add("roll");
        newCommands.add("1");
        for (String command : newCommands) {
            System.out.println("Client: " + command);
            try {
                dos.writeUTF(command);
                dos.flush();
                sleep(200);
                String serverIn = input.readUTF();
                System.out.println("Server: " + serverIn);
            } catch (Exception e) {
                System.out.println("GUI ist mit Sphero Bolt nicht verbunden!");
            }
        }
    }

    /**
     * Beendet die verbindung zum server und schließt alle Streams.
     * @throws IOException
     * @throws InterruptedException
     */
    public static void shutdown() throws IOException, InterruptedException {
        try {
            dos.writeUTF("sleep");
            dos.flush();
            sleep(200);
            socket.close();
            input.close();
            dos.close();
        } catch (Exception e) {
            System.out.println("GUI ist mit Sphero Bolt nicht verbunden!");
        }
    }
}
