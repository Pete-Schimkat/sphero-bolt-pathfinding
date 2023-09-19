import socket
import threading
import time

from bleak import BleakError
from spherov2 import scanner
from spherov2.sphero_edu import SpheroEduAPI
from spherov2.types import Color
from cv_sphero import cv_sphero

port = 11110  # Initiiert port
direction = 0 #Ausrichtung des Spheros in Winkeln
x = 0  # tracker für x-Koordinate
y = 0  # tracker für y-Koordinate
global toy


def startSphero():
    speed = 110  # Zwischen (-255,255)
    duration = 0.13  # in sek
    host = socket.gethostname()  # Get Host Name        print("start running Computer Vision ....")
    # Sphero Blau, Zeichen dass mit Client verbunden
    threading.Thread(target=cv_sphero, args=(0,)).start()

    # Zugriff auf Sphero ab hier
    with SpheroEduAPI(scanner.find_toy()) as sphero:
        # Matrix blau
        sphero.set_front_led(Color(0, 0, 255))
        sphero.set_back_led(Color(0, 0, 255))
        sphero.set_main_led(Color(r=0, g=0, b=255))
        # Server starten
        server_socket = socket.socket()
        server_socket.bind((host, port))
        print("Waiting for client to connect")
        server_socket.listen(1)
        conn, address = server_socket.accept()
        print("Server and Client are connected")
        print("Sphero is ready to receive your command \n")

        # Loop zum Empfangen von Kommandos
        activitiyIgnor = False
        while True:
            data = conn.recv(1024).decode()
            # time.sleep(2)
            if not data:
                break
            data = data.strip('\n')
            data = data[2:]
            print("Client: " + data)
            #Wenn Activity ausgeführt wurde, soll der naechste Befehl ignoriert werden
            if activitiyIgnor:
                activitiyIgnor = False
                msgToSend = "Command ignored".encode("UTF-8")
                conn.send(len(msgToSend).to_bytes(2, byteorder='big'))
                conn.send(msgToSend)
                continue
            # Zuordnung von Commands zu Ausführung
            if not activitiyIgnor:
                if data == 'spin':  # Drehen
                    # Antworten
                    msgToSend = "ok".encode("UTF-8")
                    conn.send(len(msgToSend).to_bytes(2, byteorder='big'))
                    conn.send(msgToSend)

                    # Drehwinkel empfangen
                    data = conn.recv(1024).decode()
                    data = data.strip('\n')
                    data = data[2:]

                    # Print und Verarbeitung
                    print("Client Spin: " + data)
                    global direction
                    direction = (direction + int(data)) % 360
                    sphero.spin(int(data), 0.5)
                    time.sleep(0.7)
                if data == 'roll':  # Fahren
                    # Antworten
                    msgToSend = "ok".encode("UTF-8")
                    conn.send(len(msgToSend).to_bytes(2, byteorder='big'))
                    conn.send(msgToSend)

                    # Fahrtdauer empfangen
                    data = conn.recv(1024).decode()
                    data = data.strip('\n')
                    data = data[2:]

                    # Print und Verarbeitung
                    duration = duration * int(data)
                    print("Client Roll: " + data)
                    sphero.roll(direction, speed, duration)
                    time.sleep(0.5)
                # duration = 0.1
                    setXandY()  # Aktualisieren von X und Y

                if data == 'activity':  # Einsatzaktivität ausführen
                    # Sphero Farbspiel
                    for x in range(3):
                        sphero.set_main_led(Color(r=255, g=0, b=0))
                        time.sleep(0.55)
                        sphero.set_main_led(Color(r=0, g=0, b=0))
                        time.sleep(0.55)
                        sphero.set_main_led(Color(r=0, g=255, b=0))
                        time.sleep(0.55)
                        sphero.set_main_led(Color(r=0, g=0, b=255))
                    time.sleep(1)
                    activitiyIgnor = True

                if data == 'sleep':
                    break

                # Antworten
                cv_sphero.corrector(x, y)
                msgToSend = "ok     No correction necessary ;)".encode("UTF-8")
                conn.send(len(msgToSend).to_bytes(2, byteorder='big'))
                conn.send(msgToSend)

        conn.close()  # close the connection


# X und Y aktualisieren
def setXandY():
    global x
    global y
    if direction == 90:
        y = y - 1
    if direction == 180:
        x = x - 1
    if direction == -90:
        y = y + 1
    if direction == 0:
        x = x + 1


if __name__ == '__main__':
    t = 0
    while t != 1:
        try:
            print("trying to connect to BOLT\n")
            startSphero()
        except TimeoutError or BleakError:
            print("Cannot connect to robot \ntry again")
            time.sleep(3)

        t = t + 1
    if t == 6:
        exit(-1)
