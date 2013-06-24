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


public class CreateGeneralHMM extends CreateHMM {

    /**
     * Creates a HMM with postures as observable variables  and
     * activities as hidden states.
     *
     * @param postures list of sequences of postures
     * @throws IOException
     */
    @Override
    public void createHMM(List<List<Posture>> postures) throws IOException {

        int numStates = Activity.getActivitiesNumber() + 1;
        List<String> posturesOfInterest = HumanActivity.allPosturesOfInterest;
        int numObservableVariables;

        numObservableVariables = Posture.computeNumObservableVariables(posturesOfInterest);


        HMMOperations hmmOperations = new HMMOperationsImpl();
        List<List<Integer>> observations = new ArrayList<List<Integer>>();
        List<List<Integer>> hiddenStates = new ArrayList<List<Integer>>();
        HMM hmm;

        System.out.println();
        System.out.println("General HMM");
        System.out.println();

        /* transform posture information into observable variables and hidden states*/
        for (List<Posture> sequence : postures) {

            processSequence(posturesOfInterest, observations, hiddenStates, sequence);
        }


        /* train the model using the observations and hidden states*/
        hmm = hmmOperations.trainSupervised(numStates, numObservableVariables, observations, hiddenStates);


        adjustStateProbabilitiesToEven(numStates, hmm);

        hmm.print();
        /*save the model into it's corresponding file*/
        hmm.saveModel(HMM_DIRECTORY + GENERAL_HMM_NAME + TXT_SUFFIX);

    }


    private static void processSequence(List<String> posturesOfInterest,
                                        List<List<Integer>> observations, List<List<Integer>> hiddenStates,
                                        List<Posture> sequence) {

        List<Integer> aux_o = new ArrayList<Integer>();
        List<Integer> aux_s = new ArrayList<Integer>();
        int observation;

        for (Posture posture : sequence) {


            /* transform posture information into observation index*/

            observation = posture.computeObservationIndex(posturesOfInterest);


            /* check for correct classification (incorrect classification is ignored)*/
            if (observation >= 0) {

                aux_o.add(observation);

                /* get the tagged activity, state 1 means the activity is detected, 0 otherwise*/
                aux_s.add(posture.getActivity());
            }

        }

        if (aux_o.size() > 0 && aux_s.size() > 0) {
            observations.add(aux_o);
            hiddenStates.add(aux_s);
        }
    }
}
