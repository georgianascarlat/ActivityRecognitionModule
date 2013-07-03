package app.create_hmm;


import activities.HumanActivity;
import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Posture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.*;

public class CreateHMMForEachActivity extends CreateHMM {
    @Override
    public void createHMM(List<List<Posture>> postures) throws IOException {

        for (Activity activity : Activity.values()) {
            createActivitySingleHMM(activity, postures);
        }
    }

    /**
     * Creates a HMM for a given activity using the observations deduced
     * from the list of posture sequences as observed variables and two
     * hidden states, one that means the activity has taken place and
     * one that means it hasn't.
     * <p/>
     * The learning is supervised.
     *
     * @param activity activity for which to create the HMM
     * @param postures list of sequences of postures to be used as training set
     */
    private static void createActivitySingleHMM(Activity activity, List<List<Posture>> postures) throws IOException {

        int numStates = 2;
        List<String> posturesOfInterest = HumanActivity.activityPosturesMap.get(activity);
        int numObservableVariables;
        HMMOperations hmmOperations = new HMMOperationsImpl();
        List<List<Integer>> observations = new ArrayList<List<Integer>>();
        List<List<Integer>> hiddenStates = new ArrayList<List<Integer>>();
        HMM hmm;


        numObservableVariables = Posture.computeNumObservableVariables(posturesOfInterest);


        System.out.println();
        System.out.println(activity.getName());
        System.out.println();

        /* transform posture information into observable variables and hidden states*/
        for (List<Posture> sequence : postures) {

            processSequence(activity, posturesOfInterest, observations, hiddenStates, sequence);
        }


        /* train the model using the observations and hidden states*/
        hmm = hmmOperations.trainSupervised(numStates, numObservableVariables, observations, hiddenStates);


        adjustInitialProbabilitiesToEven(numStates, hmm);

        hmm.print();
        /*save the model into it's corresponding file*/
        hmm.saveModel(HMM_DIRECTORY + activity.getName() + SINGLE_SUFFIX + TXT_SUFFIX);

    }
}
