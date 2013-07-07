package app.activity_recognition;


import app.RoomMovement;
import models.Activity;
import models.ActivityRecognitionType;
import models.Posture;
import utils.FileNameComparator;
import utils.Pair;
import utils.Utils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static utils.Utils.*;

public abstract class ActivityRecognition {

    public static RoomMovement roomMovement;
    private List<Pair<Integer, Double>> lastPredictionsWindow;

    protected ProcessPostureFile processPostureFile;
    private int waitingFrames;

    public ActivityRecognition() throws FileNotFoundException {

        lastPredictionsWindow = new LinkedList<Pair<Integer, Double>>();
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
        int currentFrameNumber = FileNameComparator.getFileNumber(postureFile);
        int frameNumber;

        waitingFrames++;
        lastPredictionsWindow.add(prediction);

        if(waitingFrames == Utils.MAX_OBSERVATION_SIZE){


            fixSequence(lastPredictionsWindow);
            for(int i=0;i<waitingFrames;i++){
                frameNumber = currentFrameNumber - waitingFrames + i + 1;

                if(frameNumber < MAX_OBSERVATION_SIZE/2){
                    outputResult(getPostureFile(frameNumber,postureFile), new Pair<Integer, Double>(0,0.8), frameNumber);
                } else
                    outputResult(getPostureFile(frameNumber,postureFile), lastPredictionsWindow.get(i), frameNumber);
            }

            waitingFrames = 0;
            lastPredictionsWindow = new LinkedList<Pair<Integer, Double>>();
        }

        if(currentFrameNumber == 131){

            for(int i=0;i<waitingFrames;i++){
                frameNumber = currentFrameNumber - waitingFrames + i + 1;
                outputResult(getPostureFile(frameNumber,postureFile), lastPredictionsWindow.get(i), frameNumber);
            }

            waitingFrames = 0;
            lastPredictionsWindow = new LinkedList<Pair<Integer, Double>>();
        }



    }

    private String getPostureFile(int frameNumber, String postureFile) {
        int index = postureFile.indexOf(POSTURE_PREFIX);
        return postureFile.substring(0,index)+POSTURE_PREFIX +frameNumber+TXT_SUFFIX;
    }

    private void fixSequence(List<Pair<Integer, Double>> lastPredictionsWindow) {

        int length = lastPredictionsWindow.size(), index, prevIndex, nextIndex;
        boolean modify = true;

        while (modify){
            modify = false;
            for(int i=1;i<length-1;i++){
                index = lastPredictionsWindow.get(i).getFirst();
                prevIndex = lastPredictionsWindow.get(i-1).getFirst();
                nextIndex = lastPredictionsWindow.get(i+1).getFirst();

                if(index!= prevIndex && index!= nextIndex){

                    lastPredictionsWindow.set(i,new Pair<Integer, Double>(prevIndex,0.3));
                    modify = true;
                }
            }
        }


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
