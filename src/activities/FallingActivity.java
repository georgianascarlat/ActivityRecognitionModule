package activities;

import models.*;
import tracking.User;
import utils.Pair;

import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;
import static app.activity_recognition.ProcessPostureFile.HUMAN_HEIGHT;
import static models.JointPoint.*;


public class FallingActivity extends HumanActivity {

    public FallingActivity() {

        this.activityType = Activity.Falling;
    }


    @Override
    protected void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {
        User user;

        try {
            user = User.readUser(skeletonFileName);
        } catch (IOException e) {
            System.out.println("No skeleton file " + skeletonFileName);
            return;
        }


        decreasingOrderAll(prediction, hmmType, user);
    }

    private void decreasingOrderAll(Prediction prediction, HMMTypes hmmType, User user) {

        boolean falling = true;
        double distance;
        Double heights[] = new Double[NUM_SKELETONS + 1];
        JointPoint jointPoints[] = {HEAD, NECK, RIGHT_SHOULDER, LEFT_SHOULDER, TORSO, LEFT_HIP, RIGHT_HIP};


        if (HUMAN_HEIGHT == null)
            return;

        for (JointPoint jointPoint : jointPoints) {

            if (!decreasingOrder(user, jointPoint)) {
                falling = false;
                break;
            }

        }


        if (falling) {
            computeLastHeights(user, heights, NECK);
            distance = Math.abs(heights[NUM_SKELETONS] - heights[0]);

            if (distance > (HUMAN_HEIGHT / 8)) {
                increaseProbability(hmmType, prediction, 0.6);

            }
        }

        updateLastUsers(user);
    }


    @Override
    protected void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {
        Pair<ObjectClass, Pair<Integer, Integer>> result1, result2, result3;
        int lastIndex = prediction.getPredictions().length - 1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();

        if (lastPrediction == 0)
            return;

        result1 = roomMovement.getMovementResult(skeletonFileName, JointPoint.LEFT_FOOT);
        result2 = roomMovement.getMovementResult(skeletonFileName, JointPoint.RIGHT_FOOT);
        result3 = roomMovement.getMovementResult(skeletonFileName, JointPoint.HEAD);


        if ((lastPosition1 != null && lastPosition1.equals(result1.getSecond()))
                && (lastPosition2 != null && lastPosition2.equals(result2.getSecond()))) {

            if (lastPosition3 != null && !lastPosition3.equals(result3.getSecond()))
                prediction.setProbability(probability * 1.2);
            else
                prediction.setProbability(probability * 1.1);
        }

        lastPosition1 = result1.getSecond();
        lastPosition2 = result2.getSecond();
        lastPosition3 = result3.getSecond();
    }
}
