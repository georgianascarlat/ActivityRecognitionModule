package app;


import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Posture;
import utils.Utils;

import java.io.IOException;
import java.util.*;

import static utils.Utils.getTrainPostures;


public class CreateHMM {


    private static final Map<Activity, List<String>> activityMap = initActivitiesMap();

    /**
     * Initialise the mapping from activities to their list of postures of interest.
     *
     * @return mapping from activities to their list of postures of interest
     */
    private static Map<Activity, List<String>> initActivitiesMap() {
        Map<Activity, List<String>> map = new EnumMap<Activity, List<String>>(Activity.class);
        List<String> interestingPostures;

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftLegFirst");
        interestingPostures.add("rightLegFirst");
        interestingPostures.add("leftLegSecond");
        interestingPostures.add("rightLegSecond");
        map.put(Activity.Walking, interestingPostures);

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        map.put(Activity.LyingDown, interestingPostures);

        return map;


    }


    public static void main(String args[]) throws IOException {

        /*read the posture information from training files*/
        List<List<Posture>> postures = getTrainPostures();


        /* create a HMM for each activity*/
        for (Activity activity : Activity.values()) {
            createActivityHMM(activity, postures);
        }

    }

    /**
     * Creates a HMM for a given activity using the observations deduced
     * from the list of postures.
     *
     * @param activity activity for which to create the HMM
     * @param postures list of sequences of postures to be used as training set
     */
    private static void createActivityHMM(Activity activity, List<List<Posture>> postures) throws IOException {

        List<String> posturesOfInterest = activityMap.get(activity);
        int numObservableVariables = Posture.computeNumObservableVariables(posturesOfInterest);
        int numStates = 2, numSequences = postures.size(), sequenceLength = postures.get(0).size();
        HMMOperations hmmOperations = new HMMOperationsImpl();
        HMM hmm;
        List<List<Integer>> observations = new ArrayList<List<Integer>>(),
                hiddenStates = new ArrayList<List<Integer>>();
        List<Integer> aux_o, aux_s;

        System.out.println();
        System.out.println(activity.getName());
        System.out.println();

        /* transform posture information into observable variables and hidden states*/
        for (List<Posture> sequence : postures) {

            aux_o = new ArrayList<Integer>();
            aux_s = new ArrayList<Integer>();

            for (Posture posture : sequence) {

                /* transform posture information into observation index*/
                int observation = posture.computeObservationIndex(posturesOfInterest);

                /* check for correct classification (incorrect classification is ignored)*/
                if (observation >= 0) {

                    aux_o.add(observation);

                    /* get the tagged activity, state 1 means the activity is detected, 0 otherwise*/
                    aux_s.add(posture.getActivity() == activity.getIndex() ? 1 : 0);
                }


            }
            observations.add(aux_o);
            hiddenStates.add(aux_s);
        }


        /* train the model using the observations and hidden states*/
        hmm = hmmOperations.trainSupervised(numStates, numObservableVariables, observations, hiddenStates);


        System.out.println(hiddenStates);
        System.out.println(observations);
        hmm.print();


        /*save the model into it's corresponding file*/
        hmm.saveModel(Utils.HMM_DIRECTORY + activity.getName() + ".txt");

    }


}
