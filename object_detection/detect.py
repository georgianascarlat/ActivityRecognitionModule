#!/usr/bin/python
#import library - MUST use cv2 if using opencv_traincascade
import cv2
import sys

# rectangle color and stroke
color = (255,0,0)       # reverse of RGB (B,G,R) - weird
strokeWeight = 1        # thickness of outline

# set window name
windowName = "Object Detection"

# load an image to search for faces
imgName = sys.argv[2];
cascadeName = sys.argv[1];
img = cv2.imread(imgName)

# load detection file (various files for different views and uses)
cascade = cv2.CascadeClassifier(cascadeName)

# preprocessing, as suggested by: http://www.bytefish.de/wiki/opencv/object_detection
# img_copy = cv2.resize(img, (img.shape[1]/2, img.shape[0]/2))
# gray = cv2.cvtColor(img_copy, cv2.COLOR_BGR2GRAY)
# gray = cv2.equalizeHist(gray)

# detect objects, return as list
rects = cascade.detectMultiScale(img)

f = open(imgName+'.txt','w')
f.write(str(len(rects))+'\n') 

# get a list of rectangles
for x,y, width,height in rects:
	f.write(str(x)+' '+str(y)+' '+str(width)+' '+str(height)+'\n') 
	cv2.rectangle(img, (x,y), (x+width, y+height), color, strokeWeight)
        
cv2.imwrite(imgName+"_detect.jpg", img)

# if esc key is hit, quit!
exit()
