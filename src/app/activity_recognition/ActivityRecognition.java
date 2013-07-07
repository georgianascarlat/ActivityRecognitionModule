package app.activity_recognition;


import app.RoomMovement;
import models.Activity;
import models.ActivityRecognitionType;
import models.Posture;
import utils.FileNameComparator;
import utils.Pair;
import utils.Utils;

import java.io.*;

import static utils.Utils.*;

public abstract class ActivityRecognition {

    public static RoomMovement roomMovement;

    protected ProcessPostureFile processPostureFile;
    private int waitingFrames;

    public ActivityRecognition() throws FileNotFoundException {

        roomMovement = new RoomMovement(ROOM_MODEL_FILE);

        processPostureFile = ProcessPostureFile.factory(HMM_TYPE);

        waitingFrames = 0;

    }

    public static void main(String args[]) throws IOException {

        factory(ACTIVITY_RECOGNITION_TYPE).activityRecognition();

    }

    public abstract void activityRecognition() throws IOException;


    protected void processNewFile(String postureFile) throws IOException {

        Pair<Integer, Double> prediction = processPostureFile.processPostureFile(postureFile);
        int frameNumber = FileNameComparator.getFileNumber(postureFile);

        if (prediction == null) {
            waitingFrames++;
            return;
        }

        if (waitingFrames > 0) {

            for (int i = 0; i < waitingFrames; i++) {
                outputResult(postureFile, prediction, frameNumber - waitingFrames + i);
            }

            waitingFrames = 0;
        }

        outputResult(postureFile, prediction, frameNumber);

    }

    private void outputResult(String postureFile, Pair<Integer, Double> prediction, int frameNumber) throws IOException {
        System.out.println("Activity from frame " + frameNumber + ": " + Activity.getActivityNameByIndex(prediction.getFirst()));

        /* log activity prediction to file */
        appendActivityToFile(frameNumber, prediction.getFirst(), prediction.getSecond(), new Posture(postureFile));
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

    protected void appendActivityToFile(int frameNumber, int predictedActivityIndex, double probability, Posture posture) throws IOException {

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Utils.ACTIVITY_FILE, true)));

        if (posture.getActivity() >= 0)
            out.println(frameNumber + "," + predictedActivityIndex + "," + probability + "," + posture.getActivity() + ",");
        else
            out.println(frameNumber + "," + predictedActivityIndex + "," + probability + ",");
        out.close();
    }


}
