__author__ = "Alireza Safdari"

import time

"""
This class contains all functions that you need for the camera and computer vision 
"""

import numpy as np
from collections import defaultdict
from color_tracker.hsv_color_detector import *
from color_tracker.visualize import *
import threading


class cv_sphero:
    def __init__(self, numberOfCamera=0, FRAME_WIDTH=640, FRAME_HEIGHT=480, areaToDetect=20):
        self.vid = cv2.VideoCapture(numberOfCamera)
        self.vid.set(cv2.CAP_PROP_FRAME_WIDTH, FRAME_WIDTH)
        self.vid.set(cv2.CAP_PROP_FRAME_HEIGHT, FRAME_HEIGHT)
        self.vid_width = int(self.vid.get(3))
        self.vid_height = int(self.vid.get(4))
        self.obj_detector = cv2.createBackgroundSubtractorMOG2(history=20, varThreshold=30)
        value = hsv_detector()
        self.HSV_LOWER_VALUE = np.array(value[0])
        self.HSV_UPPER_VALUE = np.array(value[1])
        self.areaToDetect = areaToDetect
        self.sphero_position = defaultdict(list)
        threading.Thread(target=self.objCol_tracker, args=()).start()

    @staticmethod
    def show(frame, maske2):
        cv2.imshow('bjeckt frame', frame)
        cv2.imshow("mask image", maske2)  # Displaying masks images

    """
    shows the camera as long as 'q' is printed.

    Parameters:
        argument1 boolean: detector
        argument2 int: area in pix to detect elements

    Returns:

    """
    def objCol_tracker(self, movement_detector=False):
        global contours, mask, mask_contours, mask2
        while True:
            ret, frame = self.vid.read()
            img = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)  # Converting BGR image to HSV format
            # draw_map(frame, self.vid_width, self.vid_height)
            mask = self.obj_detector.apply(frame)
            mask2 = cv2.inRange(img, self.HSV_LOWER_VALUE,
                                self.HSV_UPPER_VALUE)  # Masking the image to find our color
            mask_contours, hierarchy = cv2.findContours(mask2, cv2.RETR_EXTERNAL,
                                                        cv2.CHAIN_APPROX_SIMPLE)  # Finding contours in
            # mask image
            _, mask = cv2.threshold(mask, 254, 255, cv2.THRESH_BINARY)  # clean the mask
            contours, fack = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

            # movement_detector
            if movement_detector and len(mask_contours) != 0:
                for cnt in contours:
                    area1 = cv2.contourArea(cnt)
                    if area1 > self.areaToDetect:
                        x, y, w, h = cv2.boundingRect(cnt)
                        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 3)  # drawing rectangle

            # color detector
            for mask_contour in mask_contours:
                area2 = cv2.contourArea(mask_contour)
                if area2 > self.areaToDetect:
                    x1, y1, w1, h1 = cv2.boundingRect(mask_contour)
                    cX = abs(int((((x1 + w1) - x1) / 2.0) + x1))
                    cY = abs(int((((y1 + h1) - y1) / 2.0) + y1))
                    if len(self.sphero_position["sphero"]) == 1:
                        self.sphero_position["sphero"].pop(0)
                    self.sphero_position["sphero"].append((cX, cY))
                    draw_tracker_points(self.sphero_position["sphero"], frame)
                    cv2.rectangle(frame, (x1, y1), (x1 + w1, y1 + h1), (0, 0, 255), 1)  # drawing rectangle
                    # cv2.circle(frame, (cX, cY), 4, (0, 255, 0), 5)
                    text = "Sphero"
                    cv2.putText(frame, text, (x1, y1 - 5), cv2.FONT_HERSHEY_COMPLEX_SMALL, 1, (0, 0, 255), 1)
            self.show(frame, mask)
            if cv2.waitKey(1) == 27:
                break

        self.vid.release()
        cv2.destroyAllWindows()

    """
        returns position to corrected

    Parameters:
        argument1 int: incoming x from Algorithmus
        argument2 int: incoming y from Algorithmus
        argument2 int: a factor for correcting the difference between reality and what the algorithm sees
        argument2 int: tolerance to ignore error

    Returns: if no error 0, 0
             else to corrected x and y

    """
    def corrector(self, incoming_x, incoming_y, factor=1, tolerance=10):

        actual_position = self.sphero_position["sphero"]
        x, y = actual_position[9]
        if abs(incoming_x * factor - x) > tolerance or abs(incoming_y * factor - y) > tolerance:
            return incoming_x * factor - x, incoming_y * factor - y
        else:
            return 0, 0


if __name__ == '__main__':
    cvS = cv_sphero(0)
    time.sleep(9)
    print(cvS.corrector(10, 10, 10))
