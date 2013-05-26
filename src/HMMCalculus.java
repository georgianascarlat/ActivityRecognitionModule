import java.io.FileNotFoundException;

/**
 *
 */
public class HMMCalculus extends HMM{


    public HMMCalculus(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    protected HMMCalculus(int numStates, int numObservableVariables, double[] initialStateProbabilities, double[][] transitionMatrix, double[][] emissionMatrix) {
        super(numStates,numObservableVariables,initialStateProbabilities,transitionMatrix,emissionMatrix);
    }

    /**
     * Compute forward variables for each state at each moment of time.
     *
     * @param observations observation sequence (given as observable variable indices)
     *
     * @return forward  matrix ( numStates X numberOfObservations)
     */
    @Override
    public double[][] forward(int[] observations) {

        int finalTime = observations.length;
        double[][] forwardMatrix = new double[numStates][finalTime];


        for (int i = 0; i < numStates; i++)
            forwardMatrix[i][0] = initialStateProbabilities[i] *
                    emissionMatrix[i][observations[0]];


        for (int t = 0; t < finalTime - 1; t++) {
            for (int j = 0; j < numStates; j++) {
                forwardMatrix[j][t + 1] = 0;
                for (int i = 0; i < numStates; i++)
                    forwardMatrix[j][t + 1] += (forwardMatrix[i][t] *
                            transitionMatrix[i][j]);
                forwardMatrix[j][t + 1] *= emissionMatrix[j][observations[t + 1]];
            }
        }

        return forwardMatrix;
    }


    /**
     * Compute backward variables for each state at each moment of time.
     *
     * @param observations observation sequence (given as observable variable indices)
     *
     * @return backward  matrix ( numStates X numberOfObservations)
     */
    @Override
    public double[][] backward(int[] observations) {

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
            }
        }

        return backward;
    }



    /**
     * Computes the  probability P(Q(t) = Si, Q(t+1) = Sj | O).
     *
     * @param t   time t
     * @param i   index of state Si
     * @param j   index of state Si
     * @param observations   sequence of observations
     * @param forward the forward matrix
     * @param backward the backward matrix
     *
     * @return probability  P(Q(t) = Si, Q(t+1) = Sj | O)
     */
    @Override
    public double epsilon(int t, int i, int j, int[] observations, double[][] forward, double[][] backward) {

        double numerator;
        double denominator = 0;


        if (t == observations.length - 1)
            numerator = forward[i][t] * transitionMatrix[i][j];
        else
            numerator = forward[i][t] * transitionMatrix[i][j] * emissionMatrix[j][observations[t + 1]] * backward[j][t + 1];


        for (int k = 0; k < numStates; k++) {
            for(int l=0;l < numStates; l++){
                if (t == observations.length - 1)
                    denominator +=    forward[k][t] * transitionMatrix[k][l];
                else
                    denominator += forward[k][t] * transitionMatrix[k][l] * emissionMatrix[l][observations[t + 1]] * backward[l][t + 1];
            }
        }


        return safeDivide(numerator, denominator);
    }



    /**
     * Computes the probability P(Q(t) = Si | O).
     *
     * @param i  index of state Si
     * @param t  time t
     * @param observations    sequence of observations
     * @param forward   the forward matrix
     * @param backward  the backward matrix
     *
     * @return    probability P(Q(t) = Si | O)
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
     * Computes the probability P(O), where O = O1O2...OT.
     *
     * @param observations  sequence of observations
     *
     * @return probability P(O)
     */
    @Override
    public double observationsProbability(int[] observations) {
        double probability = 0;
        int T = observations.length;
        double[][] forward = forward(observations);

        for(int i=0;i<numStates;i++){
             probability +=forward[i][T-1];
        }

        return probability;
    }


}
