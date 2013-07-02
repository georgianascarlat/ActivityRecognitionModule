package app.object_detection;


import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_objdetect;
import models.ObjectClassifier;
import utils.Pair;
import utils.Utils;

import java.util.LinkedList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class ObjectDetection {


    public static List<Pair<Integer, Integer>> computeObjectPoints(ObjectClassifier classifierType, String fileName) {

        opencv_core.IplImage img = cvLoadImage(fileName), gray;
        CvRect rect = getFaceWithLBP(img, Utils.OBJECT_DETECTION_FOLDER + classifierType.getFileName());
        List<opencv_core.IplImage> subImages;
        List<Pair<Integer, Integer>> result = new LinkedList<Pair<Integer, Integer>>();


        gray = ImageOperations.convertToGray(img);

        gray = ImageOperations.reduceGrayValues(gray);

        subImages = ImageOperations.getSubImages(gray, rect);

        for (opencv_core.IplImage subImg : subImages) {

            ImageOperations.maskForegroundImage(subImg);

        }

        result.addAll(ImageOperations.getMaskedCoordinates(subImages, rect));

        showRect(img, rect);

        return result;
    }


    private static void showRect(IplImage src, CvRect rect) {


        for (int i = 0; i < rect.limit(); i++) {
            opencv_core.CvRect r = rect.position(i);
            cvRectangle(
                    src,
                    cvPoint(r.x(), r.y()),
                    cvPoint(r.width() + r.x(), r.height() + r.y()),
                    opencv_core.CvScalar.RED,
                    2,
                    CV_AA,
                    0);
        }


        cvShowImage("Result", src);
        cvWaitKey(0);
    }


    private static CvRect getFaceWithLBP(IplImage grayFaceImg, String CASCADE_FILE) {
        opencv_objdetect.CascadeClassifier cascade = new opencv_objdetect.CascadeClassifier(CASCADE_FILE);
        CvRect facesdetection = new CvRect();


        cascade.detectMultiScale(grayFaceImg, facesdetection, 1.1, 2, opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT | opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH,
                new CvSize(0, 0), new CvSize(grayFaceImg.width(), grayFaceImg.height()));


        return facesdetection;
    }


}
