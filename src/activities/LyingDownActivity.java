package activities;

import models.Activity;
import models.JointPoint;
import models.ObjectClass;
import models.Prediction;
import utils.Pair;

import static app.activity_recognition.ActivityRecognition.roomMovement;


public class LyingDownActivity extends HumanActivity {

    public LyingDownActivity() {

        activityType = Activity.LyingDown;
    }


    @Override
    public void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName) {

        Pair<ObjectClass, Pair<Integer, Integer>> result;
        int lastIndex = prediction.getPredictions().length - 1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();

        if (lastPrediction == 0)
            return;

        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        if (result.getFirst().equals(ObjectClass.BED)) {

            prediction.setProbability(probability * 1.5);
        }

        if (lastPosition1 != null && lastPosition1.equals(result.getSecond())) {

            prediction.setProbability(probability * 1.2);
        }

        lastPosition1 = result.getSecond();

    }


}
