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

            prediction = hmmOperations.predict(generalHMM,
                    ArrayUtils.toPrimitive((Integer[]) observations.toArray(new Integer[0])));

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

            prediction = hmmOperations.predict(generalHMM,
                    ArrayUtils.toPrimitive((Integer[]) observations.toArray(new Integer[0])));
        }

        lastIndex = prediction.getPredictions().length - 1;
        predictedActivityIndex = prediction.getPredictions()[lastIndex];
        predictedActivity = Activity.getActivityNameByIndex(predictedActivityIndex);

        System.out.println("Activity from frame " + frameNumber + ": " + predictedActivity);

        /* log activity prediction to file */
        appendActivityToFile(frameNumber, predictedActivityIndex, prediction.getProbability(), posture);


    }
}
