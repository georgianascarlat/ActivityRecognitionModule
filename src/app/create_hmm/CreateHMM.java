package app.create_hmm;


import hmm.HMM;
import models.Activity;
import models.Posture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.HMM_TYPE;
import static utils.Utils.getTrainPostures;

public abstract class CreateHMM {

    public static void main(String args[]) throws IOException {

        /*read the posture information from training files*/
        List<List<Posture>> postures = getTrainPostures();

        switch (HMM_TYPE) {

            case SpecialisedHMM:

                new CreateHMMForEachActivity().createHMM(postures);
                break;

            case GeneralHMM:

                new CreateGeneralHMM().createHMM(postures);
                break;

            case BothHMMTypes:

                new CreateGeneralHMM().createHMM(postures);
                new CreateHMMForEachActivity().createHMM(postures);
                break;

        }
    }

    public abstract void createHMM(List<List<Posture>> postures) throws IOException;

    protected static void processSequence(Activity activity, List<String> posturesOfInterest,
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
                aux_s.add(posture.getActivity() == activity.getIndex() ? 1 : 0);
            }

        }

        if (aux_o.size() > 0 && aux_s.size() > 0) {
            observations.add(aux_o);
            hiddenStates.add(aux_s);
        }
    }

    protected static void adjustStateProbabilitiesToEven(int numStates, HMM hmm) {
        double trans[][] = hmm.getTransitionMatrix();
        double initial[] = hmm.getInitialStateProbabilities();

        int div = numStates + 3;
        double otherProbability = 1.0 / div, selfProbability = (1.0 - otherProbability * (numStates - 1));
        double initialProb = 1.0 / numStates;

        for (int i = 0; i < numStates; i++) {
            initial[i] = initialProb;
            for (int j = 0; j < numStates; j++) {
                if (i == j)
                    trans[i][j] = selfProbability;
                else
                    trans[i][j] = otherProbability;
            }
        }
    }

    protected static void adjustInitialProbabilitiesToEven(int numStates, HMM hmm) {

        double initial[] = hmm.getInitialStateProbabilities();
        double initialProb = 1.0 / numStates;

        for (int i = 0; i < numStates; i++) {
            initial[i] = initialProb;

        }
    }
}
