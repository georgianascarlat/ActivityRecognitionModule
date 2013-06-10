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

    }
}
