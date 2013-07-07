package activities;

import app.activity_recognition.ProcessPostureFile;
import models.*;
import tracking.Geometry;
import tracking.User;
import utils.Pair;

import javax.vecmath.Point3d;
import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;


public class LyingDownActivity extends HumanActivity {

    public LyingDownActivity() {

        activityType = Activity.LyingDown;
    }


    @Override
    public void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        adjustPredictionBasedOnFloorDistance(prediction, skeletonFileName, hmmType);

        adjustPredictionBasedOnRoomModel(prediction, skeletonFileName, hmmType);
    }

    private void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        double probability = prediction.getProbability();
        Pair<ObjectClass, Pair<Integer, Integer>> result;

        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        if (result.getFirst().equals(ObjectClass.BED))
            increaseProbability(hmmType, prediction, 0.5);

        if (lastPosition1 != null && lastPosition1.equals(result.getSecond()))
            prediction.setProbability(probability * 1.2);

        lastPosition1 = result.getSecond();
    }

    private void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        Point3d userPoint;
        User user;
        Double meanDistance = 0.0, height;

        if (ProcessPostureFile.HUMAN_HEIGHT == null) {
            return;
        }

        height = ProcessPostureFile.HUMAN_HEIGHT / 5;

        try {
            user = User.readUser(skeletonFileName);

            for (JointPoint jointPoint : JointPoint.values()) {

                userPoint = user.getSkeletonElement(jointPoint);
                meanDistance += userPoint.distance(Geometry.projectPointOnPlan(user.getFloorNormal(), user.getFloorPoint(), userPoint));

            }

            meanDistance = meanDistance / JointPoint.jointPointNumber();

            if (meanDistance < height)
                increaseProbability(hmmType, prediction, 0.8);

            if (meanDistance > height * 1.5)
                zeroProbability(hmmType, prediction);

        } catch (IOException e) {
            System.err.println("No skeleton file found" + skeletonFileName);
        }
    }


}
