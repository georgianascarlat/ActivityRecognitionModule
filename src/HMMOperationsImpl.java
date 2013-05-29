
public class HMMOperationsImpl implements HMMOperations {

    public static final double DELTA = 0.00001;


    @Override
    public HMM train(int[] observations, int maxIterations, HMM hmm) {
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
