package activities;

import models.Activity;
import models.HMMTypes;
import models.ObjectClass;
import models.Prediction;
import tracking.Geometry;
import tracking.User;
import utils.Pair;

import java.io.IOException;

import static app.activity_recognition.ActivityRecognition.roomMovement;
import static app.activity_recognition.ProcessPostureFile.HUMAN_HEIGHT;
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
            prediction.setProbability(prediction.getProbability() * 1.2);
    }

    @Override
    protected void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        User user;
        Double lastHeights[] = new Double[NUM_SKELETONS + 1], distance;

        if (HUMAN_HEIGHT == null)
            return;

        try {
            user = User.readUser(skeletonFileName);

            if (allSkeletonsAreInitialised()) {

                computeLastHeights(user, lastHeights, TORSO);

                distance = Math.abs(lastHeights[0] - lastHeights[NUM_SKELETONS]);


                if (Geometry.descendingOrder(lastHeights)) {
                    if (distance > (HUMAN_HEIGHT / 30)) {
                        increaseProbability(hmmType, prediction, 0.8);
                    } else {
                        if (distance > (HUMAN_HEIGHT / 40))
                            increaseProbability(hmmType, prediction, 0.7);
                        else {
                            if (distance > (HUMAN_HEIGHT / 55))
                                increaseProbability(hmmType, prediction, 0.5);
                            else
                                increaseProbability(hmmType, prediction, 0.1);
                        }
                    }
                } else
                    zeroProbability(hmmType, prediction);
            }

            updateLastUsers(user);


        } catch (IOException e) {
            System.out.println("No skeleton file " + skeletonFileName);
        }
    }
}
