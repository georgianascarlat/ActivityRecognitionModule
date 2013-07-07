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
    protected void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {
        Pair<ObjectClass, Pair<Integer, Integer>> result;
        result = roomMovement.getMovementResult(skeletonFileName, JointPoint.TORSO);

        if (result.getFirst().equals(ObjectClass.BED) || result.getFirst().equals(ObjectClass.CHAIR))
            increaseProbability(hmmType, prediction, 0.5);
    }

    @Override
    protected void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        User user;
        Double lastHeights[] = new Double[NUM_SKELETONS + 1];


        try {
            user = User.readUser(skeletonFileName);

            if (allSkeletonsAreInitialised()) {

                computeLastHeights(user, lastHeights,HEAD);

                if (Geometry.descendingOrder(lastHeights))
                    increaseProbability(hmmType, prediction, 0.5);
                else
                    zeroProbability(hmmType, prediction);
            }

            updateLastUsers(user);


        } catch (IOException e) {
            System.out.println("No skeleton file " + skeletonFileName);
        }
    }
}
