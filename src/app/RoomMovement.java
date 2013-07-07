package app;


import models.JointPoint;
import models.ObjectClass;
import models.RoomModel;
import tracking.FloorProjection;
import utils.Pair;
import utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class RoomMovement {


    public static final Pair<ObjectClass, Pair<Integer, Integer>> OBJECT_CLASS_PAIR_DEFAULT = new Pair<ObjectClass, Pair<Integer, Integer>>(ObjectClass.NO_OBJECT, null);
    private RoomModel roomModel;

    public RoomMovement(String fileName) throws FileNotFoundException {

        this.roomModel = new RoomModel(fileName);
    }


    /**
     * Computes the grid cell onto which the user is projected and determines
     * which object is on that cell.
     *
     * @param skeletonFileName posture file name
     * @param jointPoint       the joint point of interest
     * @return a pair of the object class and the position on the grid(line,column)
     */
    public Pair<ObjectClass, Pair<Integer, Integer>> getMovementResult(String skeletonFileName, JointPoint jointPoint) {


        FloorProjection floorProjection;
        Pair<Integer, Integer> position;
        int line, column, objectIndex;
        ObjectClass objectClass;

        File f = new File(skeletonFileName);

        /* first check if skeleton file exists*/
        if (!f.exists()) {

            skeletonFileName = Utils.addSkeletonDeviceIndex(skeletonFileName);
            f = new File(skeletonFileName);

            if (!f.exists()) {
                System.err.println("File " + skeletonFileName + " doesn't exist.");
                return OBJECT_CLASS_PAIR_DEFAULT;
            }

        }


        try {
            floorProjection = new FloorProjection(roomModel.getWidthParts(),
                    roomModel.getHeightParts(), roomModel.getFloorWidth(), roomModel.getFloorHeight());

            /* obtain the user's position inside the grid*/
            position = floorProjection.getFloorProjection(jointPoint, skeletonFileName);
            line = position.getFirst();
            column = position.getSecond();

        /* find the object that is mapped on that position*/
            objectIndex = roomModel.getPointOnMap(line, column);
            objectClass = ObjectClass.getObjectByIndex(objectIndex);


            return new Pair<ObjectClass, Pair<Integer, Integer>>(objectClass, position);
        } catch (IOException e) {
            e.printStackTrace();
            return OBJECT_CLASS_PAIR_DEFAULT;
        }


    }
}
