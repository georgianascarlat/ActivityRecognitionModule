package activities;

import app.activity_recognition.ProcessPostureFile;
import models.Activity;
import models.JointPoint;
import models.ObjectClass;
import models.Prediction;
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
    public void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName) {

        Pair<ObjectClass, Pair<Integer, Integer>> result;

        adjustPredictionBasedOnFloorDistance(prediction, skeletonFileName);

        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        adjustPredictionBasedOnMovement(prediction, result);

        lastPosition1 = result.getSecond();

    }

    private void adjustPredictionBasedOnMovement(Prediction prediction, Pair<ObjectClass, Pair<Integer, Integer>> result) {

        double probability = prediction.getProbability();

        if (result.getFirst().equals(ObjectClass.BED)) {

            prediction.setProbability(probability * 1.5);
        }

        if (lastPosition1 != null && lastPosition1.equals(result.getSecond())) {

            prediction.setProbability(probability * 1.2);
        }
    }

    private void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName) {

        int lastIndex = prediction.getPredictions().length - 1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();
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


            if (meanDistance < height) {

                if (lastPrediction == 0) {
                    prediction.setProbability(0.8);
                    prediction.getPredictions()[lastIndex] = 1;
                } else {
                    prediction.setProbability(probability * 1.8);
                }

            }

        } catch (IOException e) {
            System.err.println("No skeleton file found" + skeletonFileName);
        }
    }


}
