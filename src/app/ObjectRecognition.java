package app;


import models.ObjectClass;
import models.RoomModel;
import tracking.Snapshot;
import utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ObjectRecognition {


    private RoomModel roomModel;

    public ObjectRecognition(String fileName) throws FileNotFoundException {

        this.roomModel = new RoomModel(fileName);
    }


    /**
     * Computes the grid cell onto which the user is projected and determines
     * which object is on that cell.
     *
     * @param skeletonFileName posture file name
     *
     * @return a pair of the object class and the position on the grid(line,column)
     */
    public Pair<ObjectClass, Pair<Integer, Integer>> getResult(String skeletonFileName) {


        Snapshot snapshot;
        Pair<Integer, Integer> position;
        int line, column, objectIndex;
        ObjectClass objectClass;

        File f = new File(skeletonFileName);

        /* first check if skeleton file exists*/
        if (!f.exists()) {
            System.err.println("File " + skeletonFileName + "doesn't exist.");
            return new Pair<ObjectClass, Pair<Integer, Integer>>(ObjectClass.NO_OBJECT, null);
        }

        /* use the skeleton file to create a Snapshot object*/
        try {
            snapshot = new Snapshot(skeletonFileName, roomModel.getWidthParts(),
                    roomModel.getHeightParts(), roomModel.getFloorWidth(), roomModel.getFloorHeight());
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<ObjectClass, Pair<Integer, Integer>>(ObjectClass.NO_OBJECT, null);
        }

        /* obtain the user's position inside the grid*/
        position = snapshot.getUserOnFloorPosition();
        line = position.getFirst();
        column = position.getSecond();

        /* find the object that is mapped on that position*/
        objectIndex = roomModel.getPointOnMap(line, column);
        objectClass = ObjectClass.getObjectByIndex(objectIndex);


        return new Pair<ObjectClass, Pair<Integer, Integer>>(objectClass, position);
    }
}
