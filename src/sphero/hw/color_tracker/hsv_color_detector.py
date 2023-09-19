__author__ = "Alireza Safdari"
""" This is a tool which you can use to easily determine the necessary HSV color values and kernel sizes for you app """

import color_tracker


def hsv_detector():
    # Init camera
    cam = color_tracker.WebCamera(video_src=0)
    cam.start_camera()

    # Init Range detector
    detector = color_tracker.HSVColorRangeDetector(camera=cam)
    lower, upper, kernel = detector.detect()

    # Print out the selected values
    # (best practice is to save as numpy arrays and then you can load it whenever you want it)
    return lower, upper, kernel.shape