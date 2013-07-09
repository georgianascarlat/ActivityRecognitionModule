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
import static models.JointPoint.HEAD;
import static models.JointPoint.TORSO;


public class StandingUpActivity extends HumanActivity {

    public StandingUpActivity() {

        this.activityType = Activity.StandingUp;
    }

    @Override
    protected void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {
        Pair<ObjectClass, Pair<Integer, Integer>> result;
        result = roomMovement.getMovementResult(skeletonFileName, TORSO);

        if (result.getFirst().equals(ObjectClass.BED) || result.getFirst().equals(ObjectClass.CHAIR))
            increaseProbability(hmmType, prediction, 0.5);
    }

    @Override
    protected void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        User user;
        Double lastHeights[] = new Double[NUM_SKELETONS + 1], distance;

        if(HUMAN_HEIGHT == null)
            return;

        try {
            user = User.readUser(skeletonFileName);

            if (allSkeletonsAreInitialised()) {

                computeLastHeights(user, lastHeights, TORSO);

                distance = Math.abs(lastHeights[0] - lastHeights[NUM_SKELETONS]);


                if (Geometry.descendingOrder(lastHeights) && distance > (HUMAN_HEIGHT/55)) {
                    increaseProbability(hmmType, prediction, 0.5);
                }
                else
                    zeroProbability(hmmType, prediction);
            }

            updateLastUsers(user);


        } catch (IOException e) {
            System.out.println("No skeleton file " + skeletonFileName);
        }
    }
}
