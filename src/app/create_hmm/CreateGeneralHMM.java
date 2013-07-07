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

    private void adjustTransitions(int numStates, HMM hmm) {

        double transition[][] = hmm.getTransitionMatrix();
        int NO_ACTIVITY = 0;
        int WALKING = Activity.Walking.getIndex();
        int LYING = Activity.LyingDown.getIndex();
        int STANDING = Activity.StandingUp.getIndex();
        int SITTING = Activity.SittingDown.getIndex();
        int BENDING = Activity.Bending.getIndex();
        int FALLING = Activity.Falling.getIndex();

        if (numStates != (Activity.getActivitiesNumber() + 1))
            return;

        transition[NO_ACTIVITY][NO_ACTIVITY] = 0.16;

        transition[NO_ACTIVITY][WALKING] = transition[NO_ACTIVITY][LYING]
                = transition[NO_ACTIVITY][STANDING]
                = transition[NO_ACTIVITY][SITTING]
                = transition[NO_ACTIVITY][BENDING]
                = transition[NO_ACTIVITY][FALLING] = 0.14;

        transition[WALKING][WALKING] = 0.5;
        transition[WALKING][LYING] = 0;
        transition[WALKING][STANDING] = 0;
        transition[WALKING][SITTING] = 0.1;
        transition[WALKING][BENDING] = 0.1;
        transition[WALKING][FALLING] = 0.1;
        transition[WALKING][NO_ACTIVITY] = 0.2;

        transition[LYING][WALKING] = 0;
        transition[LYING][LYING] = 0.6;
        transition[LYING][STANDING] = 0.15;
        transition[LYING][SITTING] = 0.15;
        transition[LYING][BENDING] = 0;
        transition[LYING][FALLING] = 0;
        transition[LYING][NO_ACTIVITY] = 0.1;

        transition[STANDING][WALKING] = 0.1;
        transition[STANDING][LYING] = 0;
        transition[STANDING][STANDING] = 0.5;
        transition[STANDING][SITTING] = 0.1;
        transition[STANDING][BENDING] = 0.1;
        transition[STANDING][FALLING] = 0.1;
        transition[STANDING][NO_ACTIVITY] = 0.1;

        transition[SITTING][WALKING] = 0;
        transition[SITTING][LYING] = 0.2;
        transition[SITTING][STANDING] = 0.2;
        transition[SITTING][SITTING] = 0.5;
        transition[SITTING][BENDING] = 0.02;
        transition[SITTING][FALLING] = 0.04;
        transition[SITTING][NO_ACTIVITY] = 0.04;

        transition[BENDING][WALKING] = 0;
        transition[BENDING][LYING] = 0;
        transition[BENDING][STANDING] = 0.15;
        transition[BENDING][SITTING] = 0.05;
        transition[BENDING][BENDING] = 0.6;
        transition[BENDING][FALLING] = 0.05;
        transition[BENDING][NO_ACTIVITY] = 0.15;

        transition[FALLING][WALKING] = 0;
        transition[FALLING][LYING] = 0.3;
        transition[FALLING][STANDING] = 0.03;
        transition[FALLING][SITTING] = 0.15;
        transition[FALLING][BENDING] = 0;
        transition[FALLING][FALLING] = 0.5;
        transition[FALLING][NO_ACTIVITY] = 0.02;


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
