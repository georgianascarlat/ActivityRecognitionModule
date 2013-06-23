package app;


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
import utils.Pair;
import utils.Utils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static activities.HumanActivity.humanActivityMap;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static utils.Utils.*;


public class ActivityRecognition {


    /* every activity has a list of observations */
    public Map<Activity, CircularFifoBuffer> activityObservationsMap = initActivityObservationsMap();

    /* the activity simple HMMs */
    public Map<Activity, HMM> activitySimpleHMMMap;

    /* the activity complex HMMs, formed out of two levels of HMMs */
    public Map<Activity, Pair<HMM, HMM>> activityComplexHMMMap;


    public static RoomMovement roomMovement;

    /* tha last position of the user on the grid*/
    private Pair<Integer, Integer> lastPosition = null;

    public ActivityRecognition() throws FileNotFoundException {

        activitySimpleHMMMap = new EnumMap<Activity, HMM>(Activity.class);
        activityComplexHMMMap = new EnumMap<Activity, Pair<HMM, HMM>>(Activity.class);

        roomMovement = new RoomMovement(Utils.ROOM_MODEL_FILE);
    }

    public static void main(String args[]) throws IOException, URISyntaxException {

        if (REAL_TIME_DETECTION)
            new ActivityRecognition().waitForNewFiles();
        else
            new ActivityRecognition().processFilesInDirectory("from_ema/data");

    }

    private void processFilesInDirectory(String directoryName) throws IOException {

        File directory = new File(directoryName);


        List<String> postureFileNames = Utils.getFileNamesFromDirectory(directory.listFiles());


        for (String fileName : postureFileNames) {
            processNewFile(fileName);
        }
    }

    /**
     * Waits for new posture files to be added in the directory DATA_DIRECTORY.
     * <p/>
     * When a new posture file is created, it processes it by predicting
     * the corresponding activity, printing and logging it into a file.
     *
     * @throws IOException
     */
    public void waitForNewFiles() throws IOException {

        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(DATA_DIRECTORY);
        String readyFileName;

        dir.register(watcher, ENTRY_CREATE);

        for (; ; ) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();


                if (kind == OVERFLOW) {
                    continue;
                }


                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                /* Verify that the new file is a text file.*/
                try {

                    Path child = dir.resolve(filename);
                    if (!Files.probeContentType(child).equals("text/plain")) {

                        continue;
                    }


                } catch (IOException x) {
                    System.err.println(x);
                    continue;
                }


                /* Verify file name */
                if (!filename.toString().startsWith(READY_PREFIX) || !filename.toString().endsWith(".txt")) {
                    continue;
                }


                try {
                    readyFileName = DATA_DIRECTORY + filename.toString();
                    processNewFile(Utils.getPostureFile(readyFileName));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            /* reset the key*/
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    /**
     * Processes a newly created file to obtain the predicted activity, which is
     * displayed and logged into the file ACTIVITY_FILE.
     * <p/>
     * When a new posture file is created, it's information is used to create
     * an observation which is added to the observations lists of all the activities.
     * For each activity, the sequence is fed to it's corresponding HMM and the
     * predictions are aggregated. The best prediction is then chosen.
     *
     * @param postureFile name of newly created file
     * @throws IOException
     */
    private void processNewFile(String postureFile) throws IOException {


        /*read posture information from file*/
        Posture posture = new Posture(postureFile);
        /* keep the predictions made by each activity HMM to later choose the best one*/
        Map<Activity, Prediction> predictions = new EnumMap<Activity, Prediction>(Activity.class);
        Prediction prediction;
        Map.Entry<Activity, Prediction> bestPrediction;
        String predictedActivity;
        int preds[], predictedActivityIndex;
        int frameNumber = FileNameComparator.getFileNumber(postureFile);

        for (Activity activity : Activity.values()) {

            /* make a prediction using an activity's HMM*/
            prediction = predictActivity(activity, posture, postureFile);

            /* may increase the probability of an activity according to the
            * information obtained from the room model: new position and object interaction*/
            if (Utils.USE_ROOM_MODEL) {
                humanActivityMap.get(activity).
                        adjustPredictionUsingRoomModel(prediction, getSkeletonFile(postureFile));
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

    private void appendActivityToFile(int frameNumber, int predictedActivityIndex, double probability, Posture posture) throws IOException {

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Utils.ACTIVITY_FILE, true)));

        if (!REAL_TIME_DETECTION && posture.getActivity() >= 0)
            out.println(frameNumber + "," + predictedActivityIndex + "," + probability + "," + posture.getActivity() + ",");
        else
            out.println(frameNumber + "," + predictedActivityIndex + "," + probability + ",");
        out.close();
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
     * @throws FileNotFoundException
     */
    private Prediction predictActivity(Activity activity, Posture posture, String postureFileName) throws FileNotFoundException {
        Prediction prediction;
        HMMOperations hmmOperations = new HMMOperationsImpl();
        List<String> posturesOfInterest = HumanActivity.activityPosturesMap.get(activity);
        int observation;

        /* obtain list of past observations */
        CircularFifoBuffer observations = activityObservationsMap.get(activity);

        /* transform posture information into observation index*/
        if (Utils.USE_CUSTOM_ACTIVITY_CLASSES) {
            observation = humanActivityMap.get(activity).getObservationClass(posture);
        } else {
            observation = posture.computeObservationIndex(posturesOfInterest);
        }

        /* if the posture is misclassified then the previous activity is detected*/
        if (observation < 0) {


            int size = observations.size();
            int size2 = size == MAX_OBSERVATION_SIZE ? size : (size + 1);
            int obs[] = new int[size2], pred[] = new int[size2];

            prediction = getPredictionFromSimpleHMM(activity, hmmOperations, observations);

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


        if (Utils.USE_SIMPLE_HMM) {

            prediction = getPredictionFromSimpleHMM(activity, hmmOperations, observations);

        } else {

            prediction = getPredictionFromComplexHMM(activity, hmmOperations, observations);

        }

        return prediction;
    }

    private Prediction getPredictionFromComplexHMM(Activity activity, HMMOperations hmmOperations, CircularFifoBuffer observations) throws FileNotFoundException {
        Pair<HMM, HMM> hmmPair;
        HMM firstLevelHMM;
        HMM secondLevelHMM;
        Prediction prediction;/* obtain the HMMs for the current activity*/
        hmmPair = activityComplexHMMMap.get(activity);


        if (hmmPair == null) {

            /* load HMMs from files if they weren't loaded before */
            firstLevelHMM = new HMMCalculus(HMM_DIRECTORY + activity.getName() + LEVEL_1_SUFFIX + TXT_SUFFIX);
            secondLevelHMM = new HMMCalculus(HMM_DIRECTORY + activity.getName() + LEVEL_2_SUFFIX + TXT_SUFFIX);

            /* keep the HMMs in the map for later use */
            activityComplexHMMMap.put(activity, new Pair<HMM, HMM>(firstLevelHMM, secondLevelHMM));

        } else {
            firstLevelHMM = hmmPair.getFirst();
            secondLevelHMM = hmmPair.getSecond();
        }

        /* predict sequence of observable variables for the second HMM */
        prediction = hmmOperations.predict(firstLevelHMM,
                ArrayUtils.toPrimitive((Integer[]) observations.toArray(new Integer[0])));

        /* predict the activity*/
        prediction = hmmOperations.predict(secondLevelHMM,
                prediction.getPredictions());
        return prediction;
    }

    private Prediction getPredictionFromSimpleHMM(Activity activity, HMMOperations hmmOperations, CircularFifoBuffer observations) throws FileNotFoundException {
        HMM hmm;
        Prediction prediction;/* obtain the HMM for the current activity*/
        hmm = activitySimpleHMMMap.get(activity);

        if (hmm == null) {

            /* load HMM from file if it wasn't loaded before */
            hmm = new HMMCalculus(HMM_DIRECTORY + activity.getName() + SINGLE_SUFFIX + TXT_SUFFIX);
            /* keep the HMM in the map for later use */
            activitySimpleHMMMap.put(activity, hmm);
        }


            /* predict */
        prediction = hmmOperations.predict(hmm,
                ArrayUtils.toPrimitive((Integer[]) observations.toArray(new Integer[0])));
        return prediction;
    }


    private Map<Activity, CircularFifoBuffer> initActivityObservationsMap() {

        Map<Activity, CircularFifoBuffer> map = new EnumMap<Activity, CircularFifoBuffer>(Activity.class);

        for (Activity activity : Activity.values()) {

            map.put(activity, new CircularFifoBuffer(Utils.MAX_OBSERVATION_SIZE));

        }

        return map;
    }


}
