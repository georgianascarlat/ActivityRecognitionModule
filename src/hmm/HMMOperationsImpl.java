package hmm;

import models.Prediction;
import models.Viterbi;
import utils.Pair;

import java.util.List;

public class HMMOperationsImpl implements HMMOperations {


    @Override
    public HMM trainSupervised(int numStates, int numObservableVariables, List<List<Integer>> observations, List<List<Integer>> hiddenStates) {
        HMM hmm = new HMMCalculus(numStates, numObservableVariables);
        int A[][] = new int[numStates][numStates];
        int B[][] = new int[numStates][numObservableVariables];
        int pi[] = new int[numStates];
        int numSequences = observations.size();
        int sequenceLength;
        int sum_pi = 0, sum_A[] = new int[numStates], sum_B[] = new int[numStates];

        /*check to see if the dimensions correspond*/
        if (numSequences != hiddenStates.size())
            throw new IllegalArgumentException("Observations and states dimensions must match");

        /*count number of appearances*/
        for (int s = 0; s < numSequences; s++) {

            /* count the number of appearances for each initial state*/
            pi[hiddenStates.get(s).get(0)]++;
            /* count total number of appearances for all initial states*/
            sum_pi++;

            sequenceLength = observations.get(s).size();

            /*check to see if the dimensions correspond*/
            if (sequenceLength != hiddenStates.get(s).size())
                throw new IllegalArgumentException("Observations and states dimensions must match");

            for (int i = 0; i < sequenceLength; i++) {

                /*count the number of appearances for each state-emission*/
                B[hiddenStates.get(s).get(i)][observations.get(s).get(i)]++;
                /*count total number of emissions from each state*/
                sum_B[hiddenStates.get(s).get(i)]++;


                if (i > 0) {
                    /*count the number of appearances for each  transition s1-s2*/
                    A[hiddenStates.get(s).get(i - 1)][hiddenStates.get(s).get(i)]++;
                    /*count the total number of transitions from s1*/
                    sum_A[hiddenStates.get(s).get(i - 1)]++;
                }
            }
        }

        /* compute probabilities based on frequency*/
        for (int i = 0; i < numStates; i++) {
            hmm.initialStateProbabilities[i] = HMM.safeDivide(pi[i], sum_pi);

            for (int j = 0; j < numStates; j++) {
                hmm.transitionMatrix[i][j] = HMM.safeDivide(A[i][j], sum_A[i]);
            }

            for (int k = 0; k < numObservableVariables; k++) {
                hmm.emissionMatrix[i][k] = HMM.safeDivide(B[i][k], sum_B[i]);
            }
        }

        return hmm;
    }


    @Override
    public Prediction predict(HMM hmm, int[] observations) {
        int T = observations.length;

        double fwd[][], bwd[][], norms[] = new double[T];
        Viterbi viterbi = hmm.viterbi(observations);
        int predictions[] = new int[T];
        double probability, max_p = 0;
        int max_i = 0;
        int numStates = hmm.getNumStates();


        for (int i = 0; i < numStates; i++) {

            if (viterbi.getDelta()[i][T - 1] > max_p) {
                max_p = viterbi.getDelta()[i][T - 1];
                max_i = i;


            }


        }

        predictions[T - 1] = max_i;
        //probability = max_p;

        fwd = hmm.forwardNormalized(observations, norms);
        bwd = hmm.backwardNormalized(observations, norms);

        // compute the probability of state i at moment T-1 given the observation sequence
        probability = hmm.gamma(max_i, T - 1, observations, fwd, bwd);

        for (int t = T - 2; t >= 0; t--) {

            predictions[t] = viterbi.getParents()[predictions[t + 1]][t + 1];

        }


        return new Prediction(observations, predictions, probability);
    }
}
