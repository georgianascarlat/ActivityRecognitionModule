package app.create_hmm;

import activities.HumanActivity;
import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Posture;
import models.Prediction;
import org.apache.commons.lang3.ArrayUtils;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.*;


public class CreateTwoLayerHMM extends CreateHMM {

    @Override
    public void createHMM(List<List<Posture>> postures) throws IOException {

        for (Activity activity : Activity.values()) {
            createActivityMultipleHMMs(activity, postures);
        }
    }

    /**
     * Creates two HMMs for an activity based on an the posture information.
     * First level HMM has as observable variables the observations deduced
     * from the posture information and the hidden states have no special meaning.
     * The second level HMM has as observable variables the hidden states obtained
     * from the first level HMM. The hidden states for this model are 1 and 0,
     * representing whether the activity has taken place(1) or not(0).
     * <p/>
     * The two models are saved into two files, with different suffixes.
     * <p/>
     * The first model is learned in an unsupervised way, and the second is
     * learned in a supervised way.
     *
     * @param activity activity for which to create the HMMs
     * @param postures list of sequences of postures to be used as training set
     * @throws IOException
     */
    private static void createActivityMultipleHMMs(Activity activity, List<List<Posture>> postures) throws IOException {


        List<String> posturesOfInterest = HumanActivity.activityPosturesMap.get(activity);
        int numStates = activity.getInternalStates();
        int numObservableVariables;
        HMMOperations operations = new HMMOperationsImpl();
        List<List<Integer>> observations = new ArrayList<List<Integer>>();
        List<List<Integer>> hiddenStates = new ArrayList<List<Integer>>();
        List<List<Integer>> newObservations = new ArrayList<List<Integer>>();
        HMM firstLevelHMM, secondLevelHMM;
        List<Integer> seq;
        Prediction prediction;
        int length;

        numObservableVariables = Posture.computeNumObservableVariables(posturesOfInterest);


        /* transform posture information into observable variables and hidden states*/
        for (List<Posture> sequence : postures) {

            processSequence(activity, posturesOfInterest, observations, hiddenStates, sequence);
        }



        /* learn first level HMM in an unsupervised way*/
        firstLevelHMM = operations.trainUnsupervisedFromMultipleObservationSequences(observations,
                Utils.MAX_LEARN_ITERATIONS, Utils.LEARN_NUM_RAND_INITS, numStates, numObservableVariables);

        System.out.println(activity.getName());
        System.out.println();
        firstLevelHMM.print();

        /*save the model into it's corresponding file*/
        firstLevelHMM.saveModel(HMM_DIRECTORY + activity.getName() + LEVEL_1_SUFFIX + TXT_SUFFIX);

        /* from the observations obtained from posture information we predict the hidden
        * states corresponding to them, which will be used as observable variables for
        * the second level HMM */
        for (List<Integer> sequence : observations) {

            prediction = operations.predict(firstLevelHMM,
                    ArrayUtils.toPrimitive(sequence.toArray(new Integer[0])));
            seq = new ArrayList<Integer>();
            length = prediction.getPredictions().length;

            for (int i = 0; i < length; i++) {

                seq.add(prediction.getPredictions()[i]);

            }

            newObservations.add(seq);

        }


        /* second level HMM has as observable variables the
         * first level HMMs hidden states and has 2 hidden
         * states representing the event that the activity
         * has taken place or not */
        numObservableVariables = numStates;
        numStates = 2;

        secondLevelHMM = operations.trainSupervised(numStates, numObservableVariables, newObservations, hiddenStates);

        //adjustStateProbabilitiesToEven(numStates, secondLevelHMM);

        System.out.println();
        secondLevelHMM.print();

        /*save the model into it's corresponding file*/
        secondLevelHMM.saveModel(HMM_DIRECTORY + activity.getName() + LEVEL_2_SUFFIX + TXT_SUFFIX);

    }
}
