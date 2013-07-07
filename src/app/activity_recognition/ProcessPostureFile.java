package app.activity_recognition;


import models.HMMTypes;
import models.JointPoint;
import tracking.Geometry;
import tracking.User;
import utils.Pair;

import javax.vecmath.Point3d;
import java.io.IOException;


public abstract class ProcessPostureFile {

    public static Double HUMAN_HEIGHT = null;


    public abstract Pair<Integer, Double> processPostureFile(String postureFileName) throws IOException;

    protected void setHumanHeight(String skeletonFile) {

        try {
            User user = User.readUser(skeletonFile);
            Point3d head = user.getSkeletonElement(JointPoint.HEAD);

            if (HUMAN_HEIGHT == null) {
                HUMAN_HEIGHT = head.distance(Geometry.projectPointOnPlan(user.getFloorNormal(), user.getFloorPoint(), head));

            }

        } catch (IOException e) {
            System.err.println("No skeleton file " + skeletonFile);
        }
    }

    public static ProcessPostureFile factory(HMMTypes hmmType) {
        switch (hmmType) {

            case SpecialisedHMM:

                return new ProcessPostureFileSpecificHMM();


            case GeneralHMM:

                return new ProcessPostureFileGeneralHMM();

            case BothHMMTypes:

                return new ProcessPostureFileBothHMMTypes();

            default:

                throw new IllegalArgumentException("No such HMM Type " + hmmType);
        }
    }
}
