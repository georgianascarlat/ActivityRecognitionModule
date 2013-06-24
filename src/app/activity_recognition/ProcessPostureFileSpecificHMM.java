package app.activity_recognition;

import activities.HumanActivity;
import models.Activity;
import models.Posture;
import models.Prediction;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import utils.FileNameComparator;
import utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static activities.HumanActivity.humanActivityMap;
import static utils.Utils.MAX_OBSERVATION_SIZE;
import static utils.Utils.getSkeletonFile;


public abstract class ProcessPostureFileSpecificHMM extends ProcessPostureFile {

    /* every activity has a list of observations */
    public Map<Activity, CircularFifoBuffer> activityObservationsMap;

    public ProcessPostureFileSpecificHMM() {

        super();
        activityObservationsMap = initActivityObservationsMap();
    }

    protected abstract Prediction getPrediction(Activity activity, CircularFifoBuffer observations) throws FileNotFoundException;

    /**
     * Processes a newly created file to obtain the predicted activity, which is
     * displayed and logged into the file ACTIVITY_FILE.
     * <p/>
     * When a new posture file is created, it's information is used to create
     * an observation which is added to the observations lists of all the activities.
     * For each activity, the sequence is fed to it's corresponding HMM and the
     * predictions are aggregated. The best prediction is then chosen.
     *
     * @param postureFileName name of newly created file
     * @throws IOException
     */
    @Override
    public void processPostureFile(String postureFileName) throws IOException {
         /*read posture information from file*/
        Posture posture = new Posture(postureFileName);
        /* keep the predictions made by each activity HMM to later choose the best one*/
        Map<Activity, Prediction> predictions = new EnumMap<Activity, Prediction>(Activity.class);
        Prediction prediction;
        Map.Entry<Activity, Prediction> bestPrediction;
        String predictedActivity;
        int preds[], predictedActivityIndex;
        int frameNumber = FileNameComparator.getFileNumber(postureFileName);

        for (Activity activity : Activity.values()) {

            /* make a prediction using an activity's HMM*/
            prediction = predictActivity(activity, posture, postureFileName);

            /* may increase the probability of an activity according to the
            * information obtained from the room model: new position and object interaction*/
            if (Utils.USE_ROOM_MODEL) {
                humanActivityMap.get(activity).
                        adjustPredictionUsingRoomModel(prediction, getSkeletonFile(postureFileName));
            }

            predictions.put(activity, prediction);
        }

        /* select the best prediction */
        bestPrediction = chooseBestPrediction(predictions);

        preds = bestPrediction.getValue().getPredictions();

        /* check to see if an activity has been detected */
        if (preds[preds.length - 1] == 1) {
            predictedActivity = bestPrediction.getKey().getName();
            predictedActivityIndex = bestPrediction.getKey().getIndex();
        } else {
            predictedActivity = "no activity";
            predictedActivityIndex = 0;
        }


        System.out.println("Activity from frame " + frameNumber + ": " + predictedActivity);

        /* log activity prediction to file */
        appendActivityToFile(frameNumber, predictedActivityIndex, bestPrediction.getValue().getProbability(), posture);
    }

    /**
     * Makes a prediction using a the HMM of an activity.
     * <p/>
     * The prediction takes into account not only the last observation,
     * but also the sequence of observations made before that.
     *
     * @param activity        activity
     * @param posture         posture information
     * @param postureFileName posture file name
     * @return prediction
     * @throws java.io.FileNotFoundException
     */
    private Prediction predictActivity(Activity activity, Posture posture, String postureFileName) throws FileNotFoundException {
        Prediction prediction;
        List<String> posturesOfInterest = HumanActivity.activityPosturesMap.get(activity);
        int observation;

        /* obtain list of past observations */
        CircularFifoBuffer observations = activityObservationsMap.get(activity);

        /* transform posture information into observation index*/
        observation = posture.computeObservationIndex(posturesOfInterest);

        /* if the posture is misclassified then the previous activity is detected*/
        if (observation < 0) {


            int size = observations.size();
            int size2 = size == MAX_OBSERVATION_SIZE ? size : (size + 1);
            int obs[] = new int[size2], pred[] = new int[size2];

            prediction = getPrediction(activity, observations);

            for (int i = 0; i < size; i++) {
                obs[i] = prediction.getObservations()[i];
            }

            for (int i = 0; i < size; i++) {
                pred[i] = prediction.getPredictions()[i];
            }
            if (size2 > size)
                pred[size] = pred[size - 1];


            return new Prediction(obs, pred, 0.01);
        }


        /* add new observation to list*/
        activityObservationsMap.remove(activity);
        observations.add(observation);
        activityObservationsMap.put(activity, observations);


        return getPrediction(activity, observations);

    }


    /**
     * Chooses from a mapping of activities and predictions the best prediction,
     * taking into account the prediction probability.
     *
     * @param predictions mapping of activities and predictions
     * @return the map entry corresponding to  the best prediction
     */
    private Map.Entry<Activity, Prediction> chooseBestPrediction(Map<Activity, Prediction> predictions) {

        Map.Entry<Activity, Prediction> prediction = null;
        int result1, result2, lastIndex;
        double probability1, probability2;


        for (Map.Entry<Activity, Prediction> entry : predictions.entrySet()) {

            if (prediction == null) {
                prediction = entry;
            } else {
                lastIndex = prediction.getValue().getObservations().length - 1;

                result1 = prediction.getValue().getPredictions()[lastIndex];
                probability1 = prediction.getValue().getProbability();
                result2 = entry.getValue().getPredictions()[lastIndex];
                probability2 = entry.getValue().getProbability();

                /*a result 1 means that an activity has been detected,
                * thus we prefer a result that predicts an activity*/
                if (result2 == 1 && result1 == 0)
                    prediction = entry;

                /* if both predict an activity, or don't predict anything then
                * we take the probability into consideration */
                if (result1 == result2) {
                    if (probability2 > probability1)
                        prediction = entry;
                }

            }

        }

        return prediction;

    }

    protected Map<Activity, CircularFifoBuffer> initActivityObservationsMap() {

        Map<Activity, CircularFifoBuffer> map = new EnumMap<Activity, CircularFifoBuffer>(Activity.class);

        for (Activity activity : Activity.values()) {

            map.put(activity, new CircularFifoBuffer(Utils.MAX_OBSERVATION_SIZE));

        }

        return map;
    }

}
