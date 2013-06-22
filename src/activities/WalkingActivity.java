package activities;

import models.*;
import utils.Pair;

import static app.ActivityRecognition.roomMovement;


public class WalkingActivity extends HumanActivity {


    public WalkingActivity() {

        activityType = Activity.Walking;
    }

    /**
     * Get the index of the observation class corresponding
     * to the posture information.
     * <p/>
     * Index 0 - class left-right feet
     * Index 1 - class right-left feet
     * Index 2 - class tight feet
     * Index 3 - class just stand
     * Index 4 - class anything else
     *
     * @param posture posture information
     * @return index of the observation class
     */
    @Override
    public int getObservationClass(Posture posture) {
        int left, right;

        // not standing straight
        if (posture.getGeneralPosture() != 1)
            return 4;

        left = posture.getLeftLegFirst();
        right = posture.getRightLegFirst();

        // left-right feet
        if (left == 3 && (right == 1 || right == 4))
            return 0;
        if (left == 1 && right == 4)
            return 0;

        // right-left feet
        if (right == 3 && (left == 1 || left == 4))
            return 1;
        if (right == 1 && left == 4)
            return 1;

        // tight feet
        if (left == 1 && right == 1)
            return 2;

        // just stand
        return 3;

    }

    @Override
    public int getObservationDomainSize() {
        return 5;
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

            prediction.setProbability(probability * 1.8);
        }

        lastPosition1 = result1.getSecond();
        lastPosition2 = result2.getSecond();
    }


}
