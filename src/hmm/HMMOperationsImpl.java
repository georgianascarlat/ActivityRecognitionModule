package hmm;

import models.Prediction;
import models.Viterbi;
import org.apache.commons.lang3.ArrayUtils;
import utils.HMMProbabilityPairComparator;
import utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HMMOperationsImpl implements HMMOperations {

    public static final double DELTA = 0.00001;


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
    public HMM trainUnsupervisedStartingFromKnownModel(int[] observations, int maxIterations, HMM hmm) {
        int T = observations.length;
        double[][] fwd;
        double[][] bwd;
        int numStates = hmm.getNumStates();
        int numObservableVariables = hmm.getNumObservableVariables();
        HMM newHMM;
        double p0, p1;


        double initialStateProbabilities[] = new double[numStates];
        double transitionMatrix[][] = new double[numStates][numStates];
        double emissionMatrix[][] = new double[numStates][numObservableVariables];

        for (int s = 0; s < maxIterations; s++) {


            /* compute and backward matrices from the current model */
            fwd = hmm.forward(observations);
            bwd = hmm.backward(observations);


            /* re-estimation of initial state probabilities */
            for (int i = 0; i < numStates; i++)
                initialStateProbabilities[i] = hmm.gamma(i, 0, observations, fwd, bwd);


             /* re-estimation of transition probabilities */
            for (int i = 0; i < numStates; i++) {
                for (int j = 0; j < numStates; j++) {
                    double numerator = 0;
                    double denominator = 0;
                    for (int t = 0; t <= T - 1; t++) {
                        numerator += hmm.epsilon(t, i, j, observations, fwd, bwd);
                        denominator += hmm.gamma(i, t, observations, fwd, bwd);
                    }
                    transitionMatrix[i][j] = HMM.safeDivide(numerator, denominator);
                }
            }


            /* re-estimation of emission probabilities */
            for (int i = 0; i < numStates; i++) {
                for (int k = 0; k < numObservableVariables; k++) {
                    double numerator = 0;
                    double denominator = 0;

                    for (int t = 0; t <= T - 1; t++) {
                        double g = hmm.gamma(i, t, observations, fwd, bwd);
                        numerator += g * (k == observations[t] ? 1 : 0);
                        denominator += g;
                    }
                    emissionMatrix[i][k] = HMM.safeDivide(numerator, denominator);
                }
            }


            newHMM = new HMMCalculus(numStates, numObservableVariables, initialStateProbabilities, transitionMatrix, emissionMatrix);

            p0 = Math.log(hmm.observationsProbability(observations));
            p1 = Math.log(newHMM.observationsProbability(observations));


            hmm = newHMM;

            if ((p1 - p0) < DELTA)
                break;


        }

        return hmm;
    }

    @Override
    public HMM trainUnsupervisedStartingFromRandom(int[] observations, int maxIterations, int numRandomInits, int numStates, int numObservableVariables) {
        HMM newHMM, hmm = new HMMCalculus(numStates, numObservableVariables);
        double p0, p1;

        for (int i = 0; i < numRandomInits; i++) {
            newHMM = new HMMCalculus(numStates, numObservableVariables);
            newHMM.randomInit();
            newHMM = trainUnsupervisedStartingFromKnownModel(observations, maxIterations, newHMM);

            p0 = hmm.observationsProbability(observations);
            p1 = newHMM.observationsProbability(observations);


            if (p1 > p0) {

                hmm = newHMM;
            }
        }

        return hmm;
    }

    @Override
    public HMM trainUnsupervisedFromMultipleObservationSequences(List<List<Integer>> observations, int maxIterations, int numRandomInits, int numStates, int numObservableVariables) {
        List<Pair<HMM, Double>> hmmList = new ArrayList<Pair<HMM, Double>>();
        List<Pair<HMM, Integer>> newHMMList = new ArrayList<Pair<HMM, Integer>>();
        HMM hmm;
        double probability, sum, lastProbability = -1;
        int index = 0;

        for (List<Integer> sequence : observations) {

            hmm = trainUnsupervisedStartingFromRandom(
                    ArrayUtils.toPrimitive(sequence.toArray(new Integer[0])),
                    maxIterations, numRandomInits, numStates, numObservableVariables);
            probability = hmm.observationsProbability(observations);
            hmmList.add(new Pair<HMM, Double>(hmm, probability));

        }

        /* sort HMMs by probability */
        Collections.sort(hmmList, new HMMProbabilityPairComparator());
        sum = 0;



        /* create a list of pairs of HMMs and their ranking, where rank
        * 1 means the worst hmm */
        for (Pair<HMM, Double> element : hmmList) {

            probability = element.getSecond();
            if (lastProbability == -1 || Math.abs(probability - lastProbability) < DELTA)
                index++;
            lastProbability = probability;
            newHMMList.add(new Pair<HMM, Integer>(element.getFirst(), index));
            sum += index;

        }


        return combineHMMs(newHMMList, sum, numStates, numObservableVariables);
    }

    /**
     * Combine multiple HMMs into one by doing a weighted average,
     * where the weights are the rank of the probabilities of the observations
     * for each HMM.
     *
     * @param hmmList                list of pairs of HMMs and their
     *                               corresponding observations probability rank
     * @param sum                    the sum of all the probability ranks for all the HMMs
     * @param numStates              number of hidden states
     * @param numObservableVariables number of observable variables
     * @return combined HMM
     */
    private HMM combineHMMs(List<Pair<HMM, Integer>> hmmList,
                            double sum, int numStates,
                            int numObservableVariables) {

        HMM combinedHMM = new HMMCalculus(numStates, numObservableVariables), hmm;
        double initialStateProbabilities[] = combinedHMM.getInitialStateProbabilities();
        double transitionMatrix[][] = combinedHMM.getTransitionMatrix();
        double emissionMatrix[][] = combinedHMM.getEmissionMatrix(), weight;

        for (Pair<HMM, Integer> element : hmmList) {

            hmm = element.getFirst();
            weight = (double) element.getSecond() / sum;

            for (int i = 0; i < numStates; i++) {

                initialStateProbabilities[i] += weight * hmm.getInitialStateProbabilities()[i];

                for (int j = 0; j < numStates; j++) {
                    transitionMatrix[i][j] += weight * hmm.getTransitionMatrix()[i][j];
                }

                for (int j = 0; j < numObservableVariables; j++) {
                    emissionMatrix[i][j] += weight * hmm.getEmissionMatrix()[i][j];
                }
            }
        }

        return combinedHMM;
    }


    @Override
    public Prediction predict(HMM hmm, int[] observations) {
        int T = observations.length;
        Viterbi viterbi = hmm.viterbi(observations);
        int predictions[] = new int[T];
        double probability, max_p = 0;
        int max_i = 0;


        for (int i = 0; i < hmm.getNumStates(); i++) {

            if (viterbi.getDelta()[i][T - 1] > max_p) {
                max_p = viterbi.getDelta()[i][T - 1];
                max_i = i;


            }


        }

        predictions[T - 1] = max_i;
        probability = max_p;

        for (int t = T - 2; t >= 0; t--) {

            predictions[t] = viterbi.getParents()[predictions[t + 1]][t + 1];

        }


        return new Prediction(observations, predictions, probability);
    }
}
