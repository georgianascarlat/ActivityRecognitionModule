package app;


import app.object_detection.ObjectDetection;
import models.ObjectClass;
import models.ObjectClassifier;
import models.RoomModel;
import tracking.FloorProjection;
import tracking.RoomInfo;
import utils.Pair;
import utils.Utils;

import javax.vecmath.Point3d;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CreateRoomModel {

    private int widthParts = 100, heightParts = 200;
    private double width = 1000, height = 5000;
    public RoomModel roomModel;
    public static final ObjectClassifier classifiers[] = {ObjectClassifier.OFFICE_CHAIR};
    public static final double minHeight = 100, maxHeight = 1000;


    public CreateRoomModel() throws FileNotFoundException {

        readRoomConfigFile();
        roomModel = new RoomModel(new RoomInfo(width, height, widthParts, heightParts));
    }

    public static void main(String args[]) throws IOException {

        CreateRoomModel createRoomModel = new CreateRoomModel();

        createRoomModel.addObjectsToRoomModel();

        createRoomModel.roomModel.saveModel(Utils.ROOM_MODEL_FILE);


    }

    public void addObjectsToRoomModel() throws FileNotFoundException {


        String depthFileName, imageFileName;
        File depthFile, imgFile;

        for (int i = 0; i < Utils.MAX_KINECT_NO; i++) {

            depthFileName = Utils.getDepthFileName(i);
            imageFileName = Utils.getImageFileName(i);

            depthFile = new File(depthFileName);
            imgFile = new File(imageFileName);

            if (depthFile.exists() && imgFile.exists()) {

                processDataFromOneKinect(depthFileName, imageFileName);

            }
        }

    }

    private void processDataFromOneKinect(String depthFile, String imageFileName) throws FileNotFoundException {

        Point3d[][] mapping = readRealWorldMapping(depthFile);
        FloorProjection floorProjection = new
                FloorProjection(roomModel.getWidthParts(), roomModel.getHeightParts(),
                roomModel.getFloorWidth(), roomModel.getFloorHeight());

        List<Pair<Integer, Integer>> auxList, pointSoFar;
        Point3d point;
        Pair<Integer, Integer> roomCell;
        Map<ObjectClass, List<Pair<Integer, Integer>>> objectPoints = new EnumMap<ObjectClass, List<Pair<Integer, Integer>>>(ObjectClass.class);
        double height;


        for (ObjectClass objectClass : ObjectClass.values()) {
            objectPoints.put(objectClass, new LinkedList<Pair<Integer, Integer>>());
        }


        for (ObjectClassifier classifier : classifiers) {

            auxList = ObjectDetection.computeObjectPoints(classifier, imageFileName);

            pointSoFar = objectPoints.get(classifier.getObjectClass());
            pointSoFar.addAll(auxList);
            objectPoints.remove(classifier.getObjectClass());

            objectPoints.put(classifier.getObjectClass(), pointSoFar);
        }

        for (ObjectClass objectClass : ObjectClass.values()) {

            pointSoFar = objectPoints.get(objectClass);

            for (Pair<Integer, Integer> objectPoint : pointSoFar) {

                point = mapping[objectPoint.getSecond()][objectPoint.getFirst()];
                height = floorProjection.getHeightFromFloor(point);

                if(height > minHeight && height < maxHeight){

                    roomCell = floorProjection.getFloorProjection(point);

                    System.out.println("H:"+floorProjection.getHeightFromFloor(point));

                    roomModel.setPointOnMap(roomCell.getFirst(), roomCell.getSecond(), objectClass.getIndex());

                }


            }
        }


    }

    public Point3d[][] readRealWorldMapping(String fileName) throws FileNotFoundException {

        Point3d[][] realWorldMapping;

        Scanner scanner = null;

        try {

            scanner = new Scanner(new File(fileName));
            scanner.useDelimiter(",|\\n");

            int error = scanner.nextInt();
            if (error == 1)
                return null;

            scanner.next();

            int height = scanner.nextInt();
            int width = scanner.nextInt();

            scanner.next();

            realWorldMapping = new Point3d[height][width];
            float a, b, c;


            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    scanner.nextInt();
                    scanner.nextInt();
                    a = scanner.nextFloat();
                    b = scanner.nextFloat();
                    c = scanner.nextFloat();
                    scanner.next();

                    realWorldMapping[y][x] = new Point3d(a, b, c);
                }
            }


            return realWorldMapping;


        } finally {
            if (null != scanner)
                scanner.close();
        }


    }

    public void readRoomConfigFile() throws FileNotFoundException {

        Scanner scanner = null;

        try {

            scanner = new Scanner(new File(Utils.ROOM_CONFIG_FILE));

            width = scanner.nextDouble();
            height = scanner.nextDouble();
            widthParts = scanner.nextInt();
            heightParts = scanner.nextInt();

        } finally {
            if (null != scanner)
                scanner.close();
        }


    }
}
