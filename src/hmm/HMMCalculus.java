package hmm;

import models.Viterbi;

import java.io.FileNotFoundException;

/**
 *
 */
public class HMMCalculus extends HMM {

    public HMMCalculus(int numStates, int numObservableVariables) {
        super(numStates, numObservableVariables);
    }

    public HMMCalculus(String fileName) throws FileNotFoundException {
        super(fileName);
    }


    /**
     * Compute normalized backward variables for each state at each moment of time.
     *
     * @param observations observation sequence (given as observable variable indices)
     * @param norms        normalization factors
     * @return backward  matrix ( numStates X numberOfObservations)
     */
    @Override
    public double[][] backwardNormalized(int[] observations, double[] norms) {

        int T = observations.length;
        double[][] backward = new double[numStates][T];


        for (int i = 0; i < numStates; i++)
            backward[i][T - 1] = 1;


        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < numStates; i++) {
                backward[i][t] = 0;
                for (int j = 0; j < numStates; j++)
                    backward[i][t] += (backward[j][t + 1] *
                            transitionMatrix[i][j] * emissionMatrix[j][observations[t + 1]]);

                backward[i][t] = safeDivide(backward[i][t], norms[t + 1]);
            }
        }

        return backward;
    }


    /**
     * Computes the probability P(Q(t) = Si | O).
     *
     * @param i            index of state Si
     * @param t            time t
     * @param observations sequence of observations
     * @param forward      the forward matrix
     * @param backward     the backward matrix
     * @return probability P(Q(t) = Si | O)
     */
    @Override
    public double gamma(int i, int t, int[] observations, double[][] forward, double[][] backward) {
        double numerator = forward[i][t] * backward[i][t];
        double denominator = 0;

        for (int j = 0; j < numStates; j++)
            denominator += forward[j][t] * backward[j][t];

        return safeDivide(numerator, denominator);
    }


    /**
     * Compute normalized forward variables for each state at each moment of time.
     * <p/>
     * Also computes the norms used in normalization so that they can be further used.
     *
     * @param observations observation sequence (given as observable variable indices)
     * @param norms        computed norms (the same size as the observations)
     * @return forward  matrix ( numStates X numberOfObservations)
     */
    @Override
    public double[][] forwardNormalized(int[] observations, double norms[]) {

        int finalTime = observations.length;
        double[][] forwardMatrix = new double[numStates][finalTime];
        double sum;

        if (norms.length != finalTime)
            throw new IllegalArgumentException("Norms must have the same size as the observations");


        norms[0] = 0;
        for (int j = 0; j < numStates; j++) {
            norms[0] += initialStateProbabilities[j] * emissionMatrix[j][observations[0]];
        }


        for (int i = 0; i < numStates; i++)
            forwardMatrix[i][0] = safeDivide(initialStateProbabilities[i] *
                    emissionMatrix[i][observations[0]], norms[0]);


        for (int t = 0; t < finalTime - 1; t++) {
            for (int j = 0; j < numStates; j++) {
                forwardMatrix[j][t + 1] = 0;


                norms[t + 1] = 0;
                for (int k = 0; k < numStates; k++) {
                    sum = 0;
                    for (int i = 0; i < numStates; i++) {
                        sum += (forwardMatrix[i][t] *
                                transitionMatrix[i][k]);
                    }
                    sum *= emissionMatrix[k][observations[t + 1]];
                    norms[t + 1] += sum;
                }

                for (int i = 0; i < numStates; i++)
                    forwardMatrix[j][t + 1] += (forwardMatrix[i][t] *
                            transitionMatrix[i][j]);
                forwardMatrix[j][t + 1] *= emissionMatrix[j][observations[t + 1]];

                forwardMatrix[j][t + 1] = safeDivide(forwardMatrix[j][t + 1], norms[t + 1]);
            }
        }

        return forwardMatrix;

    }

    /**
     * Computes delta[i][t] = max_over_q_1..q_t-1( P(q_1,..q_t = Si,O_1,..O_t)
     * and a matrix of parents used for backtracking (used by the models.Viterbi algorithm).
     *
     * @param observations observation sequence (given as observable variable indices)
     * @return object containing delta and a matrix of parents used for backtracking
     */
    @Override
    public Viterbi viterbi(int[] observations) {
        int T = observations.length;
        double delta[][] = new double[numStates][T];
        int parents[][] = new int[numStates][T];
        int max_i;
        double max_p, p;

        for (int i = 0; i < numStates; i++) {
            delta[i][0] = (initialStateProbabilities[i] * emissionMatrix[i][observations[0]]);
            parents[i][0] = 0;
        }


        for (int t = 0; t < T - 1; t++) {
            for (int j = 0; j < numStates; j++) {

                max_i = 0;
                max_p = 0;

                for (int i = 0; i < numStates; i++) {

                    p = delta[i][t] * (transitionMatrix[i][j]);

                    if (p > max_p) {

                        max_p = p;
                        max_i = i;

                    }

                }

                delta[j][t + 1] = max_p * (emissionMatrix[j][observations[t + 1]]);
                parents[j][t + 1] = max_i;

            }
        }

        return new Viterbi(delta, parents);
    }


}
