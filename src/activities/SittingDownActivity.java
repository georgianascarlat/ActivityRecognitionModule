package activities;

import app.activity_recognition.ProcessPostureFile;
import models.*;
import tracking.Geometry;
import tracking.User;
import utils.Pair;

import javax.vecmath.Point3d;
import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;
import static app.activity_recognition.ProcessPostureFile.HUMAN_HEIGHT;
import static models.JointPoint.*;
import static tracking.Geometry.ascendingOrder;
import static tracking.Geometry.distanceToFloor;
import static tracking.Geometry.projectionOnFloor;


public class SittingDownActivity extends HumanActivity {

    public SittingDownActivity() {

        this.activityType = Activity.SittingDown;
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

        compareHeadHipsTorsoWithKnees(prediction,hmmType,user);
        decreasingHeightHeadHipsTorso(prediction, hmmType, user);


    }

    private void compareHeadHipsTorsoWithKnees(Prediction prediction, HMMTypes hmmType, User user) {

        Point3d points1[] = new Point3d[4], points2[] = new Point3d[2];
        Point3d point1, point2;
        double distance;

        points1[0] = projectionOnFloor(user, HEAD);
        points1[1] = projectionOnFloor(user, LEFT_HIP);
        points1[2] = projectionOnFloor(user, RIGHT_HIP);
        points1[3] = projectionOnFloor(user, TORSO);
        points2[0] = projectionOnFloor(user, LEFT_KNEE);
        points2[1] = projectionOnFloor(user, RIGHT_KNEE);

        point1 = Geometry.median(points1);
        point2 = Geometry.median(points2);

        distance = point1.distance(point2);

        if(HUMAN_HEIGHT != null && distance> (HUMAN_HEIGHT/10))
            increaseProbability(hmmType,prediction,0.5);



    }

    private void decreasingHeightHeadHipsTorso(Prediction prediction, HMMTypes hmmType, User user) {
        Double headHeights[] = new Double[NUM_SKELETONS + 1];
        Double leftHipHeights[] = new Double[NUM_SKELETONS + 1];
        Double rightHipHeights[] = new Double[NUM_SKELETONS + 1];
        Double torsoHeights[] = new Double[NUM_SKELETONS + 1];

        if (allSkeletonsAreInitialised()) {

            computeLastHeights(user, headHeights, HEAD);
            computeLastHeights(user, leftHipHeights, LEFT_HIP);
            computeLastHeights(user, rightHipHeights, RIGHT_HIP);
            computeLastHeights(user, torsoHeights, TORSO);

            if (ascendingOrder(headHeights)
                    && ascendingOrder(leftHipHeights)
                    && ascendingOrder(rightHipHeights)
                    && ascendingOrder(torsoHeights)) {

                increaseProbability(hmmType, prediction, 0.5);
            }

        }

        updateLastUsers(user);
    }

    @Override
    protected void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {
        Pair<ObjectClass, Pair<Integer, Integer>> result;
        int lastIndex = prediction.getPredictions().length - 1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();

        if (lastPrediction == 0)
            return;

        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        if (result.getFirst().equals(ObjectClass.BED) || result.getFirst().equals(ObjectClass.CHAIR)) {

            increaseProbability(hmmType, prediction, 0.5);
        }

        if (lastPosition1 != null && lastPosition1.equals(result.getSecond())) {

            increaseProbability(hmmType, prediction, 0.2);
        }

        lastPosition1 = result.getSecond();
    }
}
