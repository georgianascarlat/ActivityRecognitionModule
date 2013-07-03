package app.activity_recognition;

import hmm.HMM;
import hmm.HMMCalculus;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Prediction;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;

import static utils.Utils.*;


public class ProcessPostureFileSpecialisedHMM extends ProcessPostureFileSpecificHMM {

    /* the activity simple HMMs */
    public Map<Activity, HMM> activitySimpleHMMMap;

    public ProcessPostureFileSpecialisedHMM() {
        super();
        activitySimpleHMMMap = new EnumMap<Activity, HMM>(Activity.class);
    }

    @Override
    protected Prediction getPrediction(Activity activity, CircularFifoBuffer observations) throws FileNotFoundException {

        HMM hmm;
        Prediction prediction;
        HMMOperations hmmOperations = new HMMOperationsImpl();

        /* obtain the HMM for the current activity*/
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
}
