package app;


import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.MovementClass;
import models.ObjectClass;
import models.Posture;
import utils.Pair;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.getTrainPostures;


public class CreateHMM {


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
     * from the list of posture sequences together with object interaction
     * information and movement information.
     *
     * @param activity activity for which to create the HMM
     * @param postures list of sequences of postures to be used as training set paired
     * with their corresponding skeleton file name
     *
     */
    private static void createActivityHMM(Activity activity, List<List<Posture>> postures) throws IOException {

        int numStates = 2;
        List<String> posturesOfInterest = Utils.activityMap.get(activity);
        int numObservableVariables = Posture.computeNumObservableVariables(posturesOfInterest);
        HMMOperations hmmOperations = new HMMOperationsImpl();
        List<List<Integer>> observations = new ArrayList<List<Integer>>();
        List<List<Integer>> hiddenStates = new ArrayList<List<Integer>>();
        HMM hmm;

        System.out.println();
        System.out.println(activity.getName());
        System.out.println();

        /* transform posture information into observable variables and hidden states*/
        for (List<Posture> sequence : postures) {

            processSequence(activity, posturesOfInterest, observations, hiddenStates, sequence);
        }


        /* train the model using the observations and hidden states*/
        hmm = hmmOperations.trainSupervised(numStates, numObservableVariables, observations, hiddenStates);


        System.out.println(hiddenStates);
        System.out.println(observations);


        adjustStateProbabilitiesToEven(numStates, hmm);

        hmm.print();
        /*save the model into it's corresponding file*/
        hmm.saveModel(Utils.HMM_DIRECTORY + activity.getName() + ".txt");

    }



    private static void processSequence(Activity activity, List<String> posturesOfInterest,
             List<List<Integer>> observations, List<List<Integer>> hiddenStates,
             List<Posture> sequence) {

        List<Integer> aux_o = new ArrayList<Integer>();
        List<Integer> aux_s = new ArrayList<Integer>();

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

    private static void adjustStateProbabilitiesToEven(int numStates, HMM hmm) {
        double trans[][] = hmm.getTransitionMatrix();
        double initial[] = hmm.getInitialStateProbabilities();

        for(int i=0;i<numStates;i++){
            initial[i] = 1.0/numStates;
            for(int j=0;j<numStates;j++){
                trans[i][j] = 1.0/numStates;
            }
        }
    }


}
