package app;

import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Integer observations1[] = {0, 2, 2, 1};
        Integer observations2[] = {1, 1, 0};
        Integer observations3[] = {1, 1, 2, 2, 0, 0, 0, 0, 1, 0, 0, 1, 0, 2, 2, 2, 0, 0, 2, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0};
        List<List<Integer>> obs = new LinkedList<List<Integer>>();
        HMMOperations operations = new HMMOperationsImpl();
        HMM hmm;


        obs.add(Arrays.asList(observations1));
        obs.add(Arrays.asList(observations2));
        obs.add(Arrays.asList(observations3));


        hmm = operations.trainUnsupervisedFromMultipleObservationSequences(obs, 10, 10, 3, 3);

        hmm.print();


        int[] observations = {1, 2};
        double norms[] = new double[2];
        double fwd[][] = hmm.forward(observations);
        for (int i = 0; i < hmm.getNumStates(); i++) {
            for (int t = 0; t < 2; t++) {
                System.out.print(fwd[i][t] + " ");
            }
            System.out.println();
        }


        System.out.println();
        fwd = hmm.forwardNormalized(observations, norms);
        double sum;
        for (int i = 0; i < hmm.getNumStates(); i++) {
            for (int t = 0; t < 2; t++) {
                System.out.print(fwd[i][t] + " ");
            }
            System.out.println();
        }

        for (int t = 0; t < 2; t++) {
            sum = 0;
            for (int i = 0; i < hmm.getNumStates(); i++) {
                sum += fwd[i][t];
            }
            System.out.println(sum);
        }

        double f1[][] = hmm.forward(observations), b1[][] = hmm.backward(observations);
        double bwd[][] = hmm.backwardNormalized(observations, norms);
        for (int i = 0; i < hmm.getNumStates(); i++) {
            for (int t = 0; t < 2; t++) {
                System.out.print(b1[i][t] * f1[i][t] / bwd[i][t] * fwd[i][t] + ":" + hmm.logObservationsProbability(observations) + "   ");
            }
            System.out.println();
        }

    }
}
