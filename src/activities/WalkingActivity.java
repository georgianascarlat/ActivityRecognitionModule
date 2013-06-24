package activities;

import models.Activity;
import models.JointPoint;
import models.ObjectClass;
import models.Prediction;
import utils.Pair;

import static app.activity_recognition.ActivityRecognition.roomMovement;


public class WalkingActivity extends HumanActivity {


    public WalkingActivity() {

        activityType = Activity.Walking;
    }


    @Override
    public void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName) {

        Pair<ObjectClass, Pair<Integer, Integer>> result1, result2;
        int lastIndex = prediction.getPredictions().length - 1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();

        if (lastPrediction == 0)
            return;

        result1 = roomMovement.getMovementResult(skeletonFileName, JointPoint.LEFT_FOOT);
        result2 = roomMovement.getMovementResult(skeletonFileName, JointPoint.RIGHT_FOOT);


        if ((lastPosition1 != null && !lastPosition1.equals(result1.getSecond()))
                || (lastPosition2 != null && !lastPosition2.equals(result2.getSecond()))) {

            prediction.setProbability(probability * 1.5);
        }

        lastPosition1 = result1.getSecond();
        lastPosition2 = result2.getSecond();
    }


}
