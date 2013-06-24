package app.activity_recognition;

import hmm.HMM;
import hmm.HMMCalculus;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Prediction;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang3.ArrayUtils;
import utils.Pair;

import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;

import static utils.Utils.*;


public class ProcessPostureFileTwoLayerHMM extends ProcessPostureFileSpecificHMM {

    /* the activity complex HMMs, formed out of two levels of HMMs */
    public Map<Activity, Pair<HMM, HMM>> activityComplexHMMMap;

    public ProcessPostureFileTwoLayerHMM() {

        super();
        activityComplexHMMMap = new EnumMap<Activity, Pair<HMM, HMM>>(Activity.class);
    }

    @Override
    protected Prediction getPrediction(Activity activity, CircularFifoBuffer observations) throws FileNotFoundException {
        Pair<HMM, HMM> hmmPair;
        HMM firstLevelHMM;
        HMM secondLevelHMM;
        Prediction prediction;
        HMMOperations hmmOperations = new HMMOperationsImpl();

        /* obtain the HMMs for the current activity*/
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
}
