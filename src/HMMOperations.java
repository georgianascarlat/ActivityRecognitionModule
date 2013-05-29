
public interface HMMOperations {

    /**
     * Train a HMM given a sequence of observations.
     *
     * @param observations  sequence of observations
     * @param maxIterations maximum number of training iterations
     * @param initialHMM    initial HMM
     * @return trained HMM
     */
    public HMM train(int[] observations, int maxIterations, HMM initialHMM);

    public Prediction predict(HMM hmm, int[] observations);
}
