package activities;

import models.*;
import tracking.Geometry;
import tracking.User;
import utils.Pair;

import javax.vecmath.Point3d;

import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;
import static models.JointPoint.*;
import static tracking.Geometry.distanceToFloor;


public class BendingActivity extends HumanActivity {

    public BendingActivity() {

        this.activityType = Activity.Bending;
    }




    @Override
    protected void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        User user;

        try {
            user = User.readUser(skeletonFileName);
        } catch (IOException e) {
            System.out.println("No such skeleton file "+skeletonFileName);
            return;
        }

        compareHipsHeights(prediction, hmmType, user,HEAD);
        compareHipsHeights(prediction, hmmType, user,NECK);

        compareHeadHipsMovements(prediction, hmmType, user);
    }

    private void compareHeadHipsMovements(Prediction prediction, HMMTypes hmmType, User user) {

        Double lastHeadHeights[] = new Double[NUM_SKELETONS + 1];
        Double lastLeftHipHeights[] = new Double[NUM_SKELETONS + 1];
        Double lastRightHipHeights[] = new Double[NUM_SKELETONS + 1];
        Double lastHipsHeights[] = new Double[NUM_SKELETONS + 1];
        double distanceHeadMovement;
        double distanceHipsMovement;
        double report;


        if (allSkeletonsAreInitialised()) {


            computeLastHeights(user, lastHeadHeights,HEAD);
            computeLastHeights(user, lastLeftHipHeights,LEFT_HIP);
            computeLastHeights(user, lastRightHipHeights,RIGHT_HIP);

            Geometry.mean(lastLeftHipHeights, lastRightHipHeights, lastHipsHeights);

            distanceHeadMovement = Math.abs(lastHeadHeights[0] - lastHeadHeights[NUM_SKELETONS]);
            distanceHipsMovement = Math.abs(lastHipsHeights[0] - lastHipsHeights[NUM_SKELETONS]);
            report = distanceHeadMovement/distanceHipsMovement;

            if (report > 3 && Geometry.ascendingOrder(lastHeadHeights) ){

                increaseProbability(hmmType, prediction, 0.6);

            }

        }

        updateLastUsers(user);
    }

    private void compareHipsHeights(Prediction prediction, HMMTypes hmmType, User user, JointPoint jointPoint) {
        Point3d head, hipLeft, hipRight;
        double headHeight, leftHipHeight, rightHipHeight;
        head = user.getSkeletonElement(jointPoint);
        hipLeft = user.getSkeletonElement(JointPoint.LEFT_HIP);
        hipRight = user.getSkeletonElement(JointPoint.RIGHT_HIP);

        headHeight = distanceToFloor(head, user);
        leftHipHeight = distanceToFloor(hipLeft, user);
        rightHipHeight = distanceToFloor(hipRight, user);

        if(headHeight < leftHipHeight && headHeight <rightHipHeight){

            increaseProbability(hmmType,prediction,0.8);
        }
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
                prediction.setProbability(probability * 1.3);

        }

        lastPosition1 = result1.getSecond();
        lastPosition2 = result2.getSecond();
        lastPosition3 = result3.getSecond();
    }
}
