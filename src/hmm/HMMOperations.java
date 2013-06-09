package hmm;

import models.Prediction;

import java.util.List;

public interface HMMOperations {


    /**
     * Train a hmm.HMM given a sequence of observations
     * and their corresponding hidden state sequence by
     * counting the frequencies.
     *
     * @param numStates              number of states of the model
     * @param numObservableVariables number of observable variables of the model
     * @param observations           sequences of observations
     * @param hiddenStates           sequences of hidden states corresponding to the observations
     * @return
     */
    public HMM trainSupervised(int numStates, int numObservableVariables,
                               List<List<Integer>> observations, List<List<Integer>> hiddenStates);

    /**
     * Train a HMM given a sequence of observations
     * using the Baum-Welch algorithm starting from
     * a known HMM.
     *
     * @param observations  sequence of observations
     * @param maxIterations maximum number of training iterations
     * @param initialHMM    initial HMM
     * @return trained HMM
     */
    public HMM trainUnsupervised(int[] observations, int maxIterations, HMM initialHMM);

    /**
     * Train a HMM given a sequence of observations
     * using the Baum-Welch algorithm.
     *
     * The training starts from  a randomly initialised
     * model. The initialisation is done many times so that
     * the model will be prevented as much as possible form
     * converging to a local maximum.
     *
     *
     * @param observations sequence of observations
     * @param maxIterations maximum number of training iterations
     * @param numRandomInits number of times the model is randomly initialised
     * @param numStates number of hidden states
     * @param numObservableVariables number of observable variables
     *
     * @return  trained HMM
     */
    public HMM trainUnsupervisedStartingFromRandom(int[] observations, int maxIterations,
                                                   int numRandomInits, int numStates,
                                                   int numObservableVariables);

    /**
     * Predict a sequence of hidden states that correspond to
     * the given observable variable sequence using the models.Viterbi
     * algorithm.
     *
     * @param hmm          Hidden Markov Model
     * @param observations sequence of observations
     * @return prediction including observations sequence,
     *         hidden state sequence and joint probability
     */
    public Prediction predict(HMM hmm, int[] observations);
}
