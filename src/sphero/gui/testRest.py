import sys
import os

args = sys.argv[1:]

if ("-argTest" in args):
    print("success!")

if ("-loremTest" in args):
    guiPath = os.path.dirname(__file__)
    fileName = "testLoremIpsum.txt"
    filePath = os.path.join(guiPath, fileName)
    f = open(filePath, 'r')
    lorem = f.read()
    print(lorem)
    f.close()
