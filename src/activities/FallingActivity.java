package activities;

import models.*;
import tracking.User;
import utils.Pair;

import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;
import static models.JointPoint.*;
import static tracking.Geometry.ascendingOrder;


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

        for(JointPoint jointPoint:JointPoint.values()){
            if(jointPoint != LEFT_FOOT && jointPoint != RIGHT_FOOT){
                if(!decreasingOrder(prediction,hmmType,user,jointPoint))
                    falling = false;
//                else
//                    System.out.println("decreasing "+jointPoint);
            }
        }

        if(falling){
//            System.out.println("Increse..");
            increaseProbability(hmmType,prediction,0.8);
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
                prediction.setProbability(probability * 1.5);
            else
                prediction.setProbability(probability * 1.1);
        }

        lastPosition1 = result1.getSecond();
        lastPosition2 = result2.getSecond();
        lastPosition3 = result3.getSecond();
    }
}
