package activities;

import app.ActivityRecognition;
import models.*;
import utils.Pair;

import static app.ActivityRecognition.roomMovement;


public class LyingDownActivity extends HumanActivity {

    public LyingDownActivity() {

        activityType = Activity.LyingDown;
    }


    /**
     * Get the index of the observation class corresponding
     * to the posture information.
     * <p/>
     * Index 0 - class horizontal
     * Index 1 - class toward horizontal
     * Index 2 - class not horizontal
     *
     * @param posture posture information
     * @return index of the observation class
     */
    @Override
    public int getObservationClass(Posture posture) {

        if (posture.getGeneralPosture() == 2)
            return 0;
        if (posture.getGeneralPosture() == 3
                || posture.getLeftLegSecond() == 2
                || posture.getRightLegSecond() == 2)
            return 1;
        return 2;

    }

    @Override
    public int getObservationDomainSize() {
        return 3;
    }

    @Override
    public void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName) {

        Pair<ObjectClass, Pair<Integer, Integer>> result;
        int lastIndex = prediction.getPredictions().length -1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();

        if(lastPrediction == 0)
            return;

        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        if(result.getFirst().equals(ObjectClass.BED)){

             prediction.setProbability(probability*1.7);
        }

        if(lastPosition1!= null && lastPosition1.equals(result.getSecond())){

            prediction.setProbability(probability*1.5);
        }

        lastPosition1 = result.getSecond();

    }


}
