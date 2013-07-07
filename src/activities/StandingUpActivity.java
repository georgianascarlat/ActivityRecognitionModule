package activities;

import models.*;
import tracking.Geometry;
import tracking.User;
import utils.Pair;

import javax.vecmath.Point3d;
import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;
import static models.JointPoint.HEAD;


public class StandingUpActivity extends HumanActivity {

    public StandingUpActivity() {

        this.activityType = Activity.StandingUp;
    }

    @Override
    public void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        adjustPredictionBasedOnFloorDistance(prediction, skeletonFileName, hmmType);

        adjustPredictionBasedOnRoomModel(prediction, skeletonFileName, hmmType);


    }

    private void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {
        Pair<ObjectClass, Pair<Integer, Integer>> result;
        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        if (result.getFirst().equals(ObjectClass.BED) || result.getFirst().equals(ObjectClass.CHAIR))
            increaseProbability(hmmType, prediction, 0.5);
    }

    private void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        User user;
        Double lastHeights[] = new Double[NUM_SKELETONS + 1];
        Point3d point;

        try {
            user = User.readUser(skeletonFileName);

            if (allSkeletonsAreInitialised()) {

                point = user.getSkeletonElement(HEAD);
                lastHeights[0] = point.distance(Geometry.projectPointOnPlan(user.getFloorNormal(), user.getFloorPoint(), point));

                for (int i = 0; i < 3; i++) {
                    point = lastUserSkeletons[i].getSkeletonElement(HEAD);
                    lastHeights[i + 1] = point.distance(Geometry.projectPointOnPlan(user.getFloorNormal(), user.getFloorPoint(), point));
                }

                if (Geometry.descendingOrder(lastHeights))
                    increaseProbability(hmmType, prediction, 0.8);
                else
                    zeroProbability(hmmType, prediction);
            }

            for (int i = (NUM_SKELETONS - 1); i > 0; i--)
                lastUserSkeletons[i] = lastUserSkeletons[i - 1];

            lastUserSkeletons[0] = user;


        } catch (IOException e) {
            System.out.println("No skeleton file " + skeletonFileName);
        }
    }
}
