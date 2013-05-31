
public interface HMMOperations {


    /**
     * Train a HMM given a sequence of observations
     * and their corresponding hidden state sequence.
     *
     * @param numStates  number of states of the model
     * @param numObservableVariables number of observable variables of the model
     * @param observations  sequences of observations
     * @param hiddenStates  sequences of hidden states corresponding to the observations
     * @return
     */
    public HMM trainSupervised(int numStates, int numObservableVariables,
                               int[][] observations, int[][] hiddenStates);

    /**
     * Train a HMM given a sequence of observations.
     *
     * @param observations  sequence of observations
     * @param maxIterations maximum number of training iterations
     * @param initialHMM    initial HMM
     * @return trained HMM
     */
    public HMM trainUnsupervised(int[] observations, int maxIterations, HMM initialHMM);

    /**
     *
     * Predict a sequence of hidden states that correspond to
     * the given observable variable sequence.
     *
     * @param hmm Hidden Markov Model
     * @param observations  sequence of observations
     *
     * @return  prediction including observations sequence,
     * hidden state sequence and joint probability
     */
    public Prediction predict(HMM hmm, int[] observations);
}
