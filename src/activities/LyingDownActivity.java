package activities;

import app.activity_recognition.ProcessPostureFile;
import models.*;
import tracking.Geometry;
import tracking.User;
import utils.Pair;

import javax.vecmath.Point3d;
import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;
import static tracking.Geometry.distanceToFloor;


public class LyingDownActivity extends HumanActivity {

    public LyingDownActivity() {

        activityType = Activity.LyingDown;
    }



    @Override
    protected void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        double probability = prediction.getProbability();
        Pair<ObjectClass, Pair<Integer, Integer>> result;

        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        if (result.getFirst().equals(ObjectClass.BED))
            increaseProbability(hmmType, prediction, 0.5);

        if (lastPosition1 != null && lastPosition1.equals(result.getSecond()))
            prediction.setProbability(probability * 1.2);

        lastPosition1 = result.getSecond();
    }

    @Override
    protected void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        Point3d userPoint;
        User user;
        Double meanDistance = 0.0, height, headHeight;

        if (ProcessPostureFile.HUMAN_HEIGHT == null) {
            return;
        }

        height = ProcessPostureFile.HUMAN_HEIGHT / 5;

        try {
            user = User.readUser(skeletonFileName);

            for (JointPoint jointPoint : JointPoint.values()) {

                userPoint = user.getSkeletonElement(jointPoint);
                meanDistance += distanceToFloor(userPoint, user);

            }

            meanDistance = meanDistance / JointPoint.jointPointNumber();

            userPoint = user.getSkeletonElement(JointPoint.HEAD);
            headHeight = distanceToFloor(userPoint, user);


            if (meanDistance < height && (headHeight / meanDistance) < 2)
                increaseProbability(hmmType, prediction, 0.8);

            if (meanDistance > height * 1.5)
                zeroProbability(hmmType, prediction);

            if ((headHeight / meanDistance) >= 2)
                decreaseProbability(hmmType, prediction, 0.3);

        } catch (IOException e) {
            System.err.println("No skeleton file found" + skeletonFileName);
        }
    }


}
