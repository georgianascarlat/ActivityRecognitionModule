package activities;

import models.Activity;
import models.HMMTypes;
import models.JointPoint;
import models.Prediction;
import tracking.Geometry;
import tracking.User;

import javax.vecmath.Point3d;
import java.io.IOException;

import static app.activity_recognition.ProcessPostureFile.HUMAN_HEIGHT;
import static models.JointPoint.*;


public class WalkingActivity extends HumanActivity {


    public static final JointPoint[] jointPoints = {HEAD, NECK, LEFT_SHOULDER, RIGHT_SHOULDER, TORSO, LEFT_HIP, RIGHT_HIP};

    public WalkingActivity() {

        activityType = Activity.Walking;
    }

    @Override
    protected void adjustPredictionBasedOnFloorDistance(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

        boolean adjust = true;
        Point3d points[] = new Point3d[NUM_SKELETONS + 1], point;
        User user;
        double distance = 0;

        if (HUMAN_HEIGHT == null)
            return;

        try {
            user = User.readUser(skeletonFileName);
        } catch (IOException e) {
            System.err.println("No such skeleton file " + skeletonFileName);
            return;
        }

        if (allSkeletonsAreInitialised()) {
            for (JointPoint jointPoint : jointPoints) {

                point = user.getSkeletonElement(jointPoint);
                points[0] = Geometry.projectPointOnPlan(user.getFloorNormal(), user.getFloorPoint(), point);

                for (int i = 0; i < NUM_SKELETONS; i++) {
                    point = lastUserSkeletons[i].getSkeletonElement(jointPoint);
                    points[i + 1] = Geometry.projectPointOnPlan(user.getFloorNormal(), user.getFloorPoint(), point);
                }

                if (!Geometry.arePointsInOrder(points)) {
                    adjust = false;
                    break;
                }

                if (jointPoint == TORSO)
                    distance = points[0].distance(points[NUM_SKELETONS]);

            }
        }

        if (adjust && distance > (HUMAN_HEIGHT / 80)) {
            increaseProbability(hmmType, prediction, 0.5);
        } else {
            decreaseProbability(hmmType, prediction, 0.3);
        }

        updateLastUsers(user);


    }

    @Override
    protected void adjustPredictionBasedOnRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType) {

    }

}
