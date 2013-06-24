package app.activity_recognition;


import activities.HumanActivity;
import hmm.HMM;
import hmm.HMMCalculus;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Posture;
import models.Prediction;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang3.ArrayUtils;
import utils.FileNameComparator;
import utils.Utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static activities.HumanActivity.humanActivityMap;
import static utils.Utils.*;

public class ProcessPostureFileGeneralHMM extends ProcessPostureFile {

    public CircularFifoBuffer generalHMMObservation;
    public HMM generalHMM;

    public ProcessPostureFileGeneralHMM() {
        generalHMMObservation = new CircularFifoBuffer(Utils.MAX_OBSERVATION_SIZE);
    }

    /**
     * Reads posture information from file, converts it to an observation
     * index and predicts the associated activity.
     *
     * @param postureFileName posture file name
     * @throws IOException
     */
    @Override
    public void processPostureFile(String postureFileName) throws IOException {

        /*read posture information from file*/
        Posture posture = new Posture(postureFileName);
        int observation, lastIndex, predictedActivityIndex;
        CircularFifoBuffer observations;
        Prediction prediction;
        HMMOperations hmmOperations = new HMMOperationsImpl();
        int frameNumber = FileNameComparator.getFileNumber(postureFileName);
        String predictedActivity;

        if (generalHMM == null) {
            /* load HMM from file if it wasn't loaded before */
            generalHMM = new HMMCalculus(HMM_DIRECTORY + GENERAL_HMM_NAME + TXT_SUFFIX);

        }

        observations = generalHMMObservation;

        /* compute observation index from posture information*/
        observation = posture.computeObservationIndex(HumanActivity.allPosturesOfInterest);

        if (observation < 0) {


            int size = observations.size();
            int size2 = size == MAX_OBSERVATION_SIZE ? size : (size + 1);
            int obs[] = new int[size2], pred[] = new int[size2];

            prediction = getPrediction(observations, hmmOperations, postureFileName);

            for (int i = 0; i < size; i++) {
                obs[i] = prediction.getObservations()[i];
            }

            for (int i = 0; i < size; i++) {
                pred[i] = prediction.getPredictions()[i];
            }
            if (size2 > size)
                pred[size] = pred[size - 1];


            prediction = new Prediction(obs, pred, 0.001);

        } else {

            observations.add(observation);

            prediction = getPrediction(observations, hmmOperations, postureFileName);
        }

        lastIndex = prediction.getPredictions().length - 1;
        predictedActivityIndex = prediction.getPredictions()[lastIndex];
        predictedActivity = Activity.getActivityNameByIndex(predictedActivityIndex);

        System.out.println("Activity from frame " + frameNumber + ": " + predictedActivity);

        /* log activity prediction to file */
        appendActivityToFile(frameNumber, predictedActivityIndex, prediction.getProbability(), posture);


    }

    private Prediction getPrediction(CircularFifoBuffer observations, HMMOperations hmmOperations, String postureFileName) {
        Prediction prediction;
         /* keep the predictions made by each activity HMM to later choose the best one*/
        List<Prediction> activityPredictions = new LinkedList<Prediction>();
        int T = observations.size(), numActivities = Activity.getActivitiesNumber();
        double fwd[][], bwd[][], norms[] = new double[T], probability;
        int obs[] = ArrayUtils.toPrimitive((Integer[]) observations.toArray(new Integer[0]));
        int predictions[] = new int[T];

        if (Utils.USE_ROOM_MODEL) {

            fwd = generalHMM.forwardNormalized(obs, norms);
            bwd = generalHMM.backwardNormalized(obs, norms);

            for (int i = 0; i <= numActivities; i++) {
                // compute the probability of the activity with index i at moment T-1 given the observation sequence
                probability = generalHMM.gamma(i, T - 1, obs, fwd, bwd);
                predictions[T - 1] = i;

                prediction = new Prediction(obs, predictions, probability);


                /* may increase the probability of an activity according to the
                * information obtained from the room model: new position and object interaction*/
                if (i > 0) {
                    humanActivityMap.get(Activity.getActivityByIndex(i)).
                            adjustPredictionUsingRoomModel(prediction, getSkeletonFile(postureFileName));
                }

                activityPredictions.add(prediction);

            }

            prediction = chooseBestPrediction(activityPredictions);


        } else {
            prediction = hmmOperations.predict(generalHMM, obs);
        }

        return prediction;
    }

    private Prediction chooseBestPrediction(List<Prediction> activityPredictions) {

        Prediction bestPrediction = activityPredictions.get(0);
        double max = bestPrediction.getProbability();

        for (Prediction prediction : activityPredictions) {
            if (prediction.getProbability() > max) {
                max = prediction.getProbability();
                bestPrediction = prediction;
            }
        }

        return bestPrediction;


    }
}
