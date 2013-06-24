package app.activity_recognition;


import app.RoomMovement;
import models.ActivityRecognitionType;

import java.io.FileNotFoundException;
import java.io.IOException;

import static utils.Utils.*;

public abstract class ActivityRecognition {

    public static RoomMovement roomMovement;

    protected ProcessPostureFile processPostureFile;

    public ActivityRecognition() throws FileNotFoundException {

        roomMovement = new RoomMovement(ROOM_MODEL_FILE);

        processPostureFile = ProcessPostureFile.factory(HMM_TYPE);
    }

    public static void main(String args[]) throws IOException {

        factory(ACTIVITY_RECOGNITION_TYPE).activityRecognition();

    }

    public abstract void activityRecognition() throws IOException;


    protected void processNewFile(String postureFile) throws IOException {
        processPostureFile.processPostureFile(postureFile);
    }

    public static ActivityRecognition factory(ActivityRecognitionType type) throws FileNotFoundException {
        switch (type) {

            case ONLINE_RECOGNITION:

                return new ActivityRecognitionOnline();

            case OFFLINE_RECOGNITION:

                return new ActivityRecognitionOffline();

            default:
                throw new IllegalArgumentException("No such Activity Recognition type " + type);
        }
    }


}
