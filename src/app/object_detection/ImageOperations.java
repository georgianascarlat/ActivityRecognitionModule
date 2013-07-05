package app.object_detection;


import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_imgproc;
import utils.Pair;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class ImageOperations {

    public static final int REDUCE_FACTOR = 5;
    private static final int DOWN_FACTOR = 64;





    public static List<opencv_core.IplImage> getSubImages(opencv_core.IplImage image, opencv_core.CvRect rect) {

        List<opencv_core.IplImage> list = new LinkedList<opencv_core.IplImage>();
        opencv_core.IplImage aux;

        for (int i = 0; i < rect.limit(); i++) {


            opencv_core.CvRect r = rect.position(i);

            cvSetImageROI(image, r);

            aux = cvCreateImage(cvGetSize(image), image.depth(), image.nChannels());

            cvCopy(image, aux);
            cvResetImageROI(image);


            //cvSaveImage(i+"_.jpg",aux);

            list.add(aux);

        }

        return list;
    }


    public static opencv_core.IplImage convertToGray(opencv_core.IplImage image) {


        opencv_core.IplImage result = cvCreateImage(cvGetSize(image), image.depth(), 1);
        cvCvtColor(image, result, opencv_imgproc.CV_RGB2GRAY);


        return result;
    }


    public static opencv_core.IplImage reduceGrayValues(opencv_core.IplImage image) {
        opencv_core.IplImage result = cvCreateImage(cvGetSize(image), image.depth(), image.nChannels());
        cvCopy(image, result);
        ByteBuffer buffer = result.getByteBuffer();

        for (int y = 0; y < result.height(); y++) {
            for (int x = 0; x < result.width(); x++) {
                int index = y * result.widthStep() + x * result.nChannels();

                // Used to read the pixel value - the 0xFF is needed to cast from
                // an unsigned byte to an int.
                int value = buffer.get(index) & 0xFF;


                // Sets the pixel to a value (greyscale).
                buffer.put(index, (byte) ((value / DOWN_FACTOR) * DOWN_FACTOR));


            }
        }

        return result;

    }

    private static int getMostFrequentColor(opencv_core.IplImage image) {
        int color = 0, maxCounts = 0;
        int hist[] = new int[256];

        ByteBuffer buffer = image.getByteBuffer();

        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                int index = y * image.widthStep() + x * image.nChannels();

                // Used to read the pixel value - the 0xFF is needed to cast from
                // an unsigned byte to an int.
                int value = buffer.get(index) & 0xFF;
                hist[value]++;
                if (hist[value] > maxCounts) {
                    maxCounts = hist[value];
                    color = value;
                }

            }
        }

        return color;
    }

    public static void maskForegroundImage(opencv_core.IplImage image) {

        cvAdaptiveThreshold(image, image, 255, opencv_imgproc.CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 7, 5);

    }

    public static List<Pair<Integer, Integer>> getMaskedCoordinates(List<IplImage> images, CvRect rect) {
        List<Pair<Integer, Integer>> result = new LinkedList<Pair<Integer, Integer>>();
        int numImages = images.size(), xCoord, yCoord;
        ByteBuffer buffer;
        opencv_core.IplImage image;

        if (numImages != rect.limit())
            throw new IllegalArgumentException("Sizes of images and rectangles don't match");

        for (int i = 0; i < numImages; i++) {

            opencv_core.CvRect r = rect.position(i);
            image = images.get(i);
            buffer = image.getByteBuffer();

            for (int y = 0; y < image.height(); y++) {
                for (int x = 0; x < image.width(); x++) {
                    int index = y * image.widthStep() + x * image.nChannels();

                    // Used to read the pixel value - the 0xFF is needed to cast from
                    // an unsigned byte to an int.
                    int value = buffer.get(index) & 0xFF;

                    if (value == 0) {

                        xCoord = x + r.x();
                        yCoord = y + r.y();
                        result.add(new Pair<Integer, Integer>(xCoord, yCoord));

                    }

                }
            }
        }

        return result;
    }

    private static opencv_core.CvRect makeRectSmaller(opencv_core.CvRect rect) {

        opencv_core.CvRect smallerRect = new opencv_core.CvRect(rect);
        double dx = (double) smallerRect.width() / REDUCE_FACTOR, dy = (double) smallerRect.height() / REDUCE_FACTOR;
        int idx = (int) Math.round(dx), idy = (int) Math.round(dy);

        for (int i = 0; i < smallerRect.limit(); i++) {
            opencv_core.CvRect r = smallerRect.position(i);

            r.x(r.x() + idx).y(r.y() + idy).width(r.width() - 2 * idx).height(r.height() - 2 * idy);

        }

        return smallerRect;
    }

    public static CvRect mergeRectangles(CvRect rect) {

        CvRect finalRects = new CvRect(rect.limit()), r1, r2, rr = new CvRect(rect);

        int index = 0;
        List<Integer> added = new LinkedList<Integer>();



        for(int i=0;i<rect.limit();i++){

            if(added.contains(i))
                continue;

            r1 = rect.position(i);


            for(int j=i+1; j <rr.limit();j++){

                if(added.contains(j))
                    continue;

                r2 = rr.position(j);

                if(intersectRects(r1,r2)){

                    r1 = mergeRects(r1,r2);
                    added.add(j);

                }
            }

            addRect(finalRects,r1,index);
            index++;

        }

        finalRects.limit(index);

        return finalRects;


    }

    private static void addRect(CvRect finalRects, CvRect r1, int index) {

        finalRects.position(index).put(new CvRect().x(r1.x()).y(r1.y()).width(r1.width()).height(r1.height()));

    }

    private static CvRect mergeRects(CvRect r1, CvRect r2) {

        int x = Math.min(r1.x(), r2.x());
        int y = Math.min(r1.y(), r2.y());
        int width = Math.max(r1.x()+r1.width(), r2.x()+r2.width()) - x;
        int height =  Math.max(r1.y()+r1.height(), r2.y()+r2.height()) - y;
        return new CvRect(x, y,width,height);
    }

    private static boolean intersectRects(CvRect r1, CvRect r2) {

        int intersect = 0;



        if(r1.x() < r2.x()){
            if(r2.x() <= (r1.x()+r1.width()))
                intersect++;
        } else {
            if(r1.x() <= (r2.x()+r2.width()))
                intersect++;
        }

        if(r1.y() < r2.y()){
            if(r2.y() <= (r1.y()+r1.height()))
                intersect++;
        } else {
            if(r1.y() <= (r2.y()+r2.height()))
                intersect++;
        }

        return intersect == 2;
    }
}
