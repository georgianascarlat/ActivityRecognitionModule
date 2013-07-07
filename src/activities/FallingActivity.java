package activities;

import models.*;
import utils.Pair;

import static app.activity_recognition.ActivityRecognition.roomMovement;


public class FallingActivity extends HumanActivity {

    public FallingActivity() {

        this.activityType = Activity.Falling;
    }


    @Override
    public void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {
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
                prediction.setProbability(probability * 1.5);
            else
                prediction.setProbability(probability * 1.1);
        }

        lastPosition1 = result1.getSecond();
        lastPosition2 = result2.getSecond();
        lastPosition3 = result3.getSecond();
    }
}
