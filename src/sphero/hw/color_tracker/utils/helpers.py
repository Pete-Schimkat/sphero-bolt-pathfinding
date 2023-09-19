__author__ = "Alireza Safdari"
import cv2
import numpy as np


def resize_img(image: np.ndarray, min_width: int, min_height: int) -> np.ndarray:
    """
    Resize the image with keeping the aspect ratio.
    :param image: image
    :param min_width: minimum width of the image
    :param min_height: minimum height of the image
    :return: resized image
    """

    h, w = image.shape[:2]

    new_w = w
    new_h = h

    if w > min_width:
        new_w = min_width
        new_h = int(h * (float(new_w) / w))

    h, w = (new_h, new_w)
    if h > min_height:
        new_h = min_height
        new_w = int(w * (float(new_h) / h))

    return cv2.resize(image, (new_w, new_h))
