package activities;

import models.*;
import utils.Pair;

import static app.ActivityRecognition.roomMovement;


public class StandingUpActivity extends HumanActivity {

    public StandingUpActivity() {

        this.activityType = Activity.StandingUp;
    }


    @Override
    public int getObservationClass(Posture posture) {

        // the classes are the same
        return posture.computeObservationIndex(activityPosturesMap.get(activityType));
    }

    @Override
    public int getObservationDomainSize() {

        // the classes are the same
        return Posture.computeNumObservableVariables(activityPosturesMap.get(activityType));
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

        if (result.getFirst().equals(ObjectClass.BED) || result.getFirst().equals(ObjectClass.CHAIR)) {

            prediction.setProbability(probability * 1.5);
        }

        if (lastPosition1 != null && lastPosition1.equals(result.getSecond())) {

            prediction.setProbability(probability * 1.2);
        }

        lastPosition1 = result.getSecond();
    }
}
