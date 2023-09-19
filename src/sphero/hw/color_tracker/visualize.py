__author__ = "Alireza Safdari"
import colorsys
import random
from typing import Tuple

import cv2


def random_color(nb_of_colors: int = 10, brightness: float = 1.0):
    hsv = [(i / nb_of_colors, 1, brightness) for i in range(nb_of_colors)]
    colors = list(map(lambda c: colorsys.hsv_to_rgb(*c), hsv))
    # note: we need to use list here with values [0, 255] as python built in scalar types,
    # because OpenCV functions can't get numpy dtypes for color
    colors = [list(map(lambda x: int(x * 255), c)) for c in colors]
    random.shuffle(colors)
    return tuple(colors[random.randint(0, len(colors) - 1)])


def draw_tracker_points(points, debug_image, color: [int, int, int] = random_color(10)):
    for i in range(1, len(points)):
        if points[i - 1] is None or points[i] is None:
            continue
        if points[i] != points[i - 1]:
            rectangle_offset = 4
            rectangle_pt1 = tuple(x - rectangle_offset for x in points[i])
            rectangle_pt2 = tuple(x + rectangle_offset for x in points[i])
            cv2.rectangle(debug_image, rectangle_pt1, rectangle_pt2, color, 1)
            cv2.line(debug_image, tuple(points[i - 1]), tuple(points[i]), color, 1)


def draw_map(frame, vid_width, vid_height):
    cv2.line(frame, (0, 0), (vid_width, vid_height), (0, 0, 255), 1)
    cv2.line(frame, (vid_width, 0), (0, vid_height), (0, 0, 255), 1)
    cv2.line(frame, (int(vid_width / 2), 0), (int(vid_width / 2), vid_height), (255, 0, 0), 1)
    cv2.line(frame, (0, int(vid_height / 2)), (vid_width, int(vid_height / 2)), (255, 0, 0), 1)

