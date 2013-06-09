package app;

import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Posture;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        int observations[] = {0,2,2,1};
        HMMOperations operations = new HMMOperationsImpl();
        HMM hmm = operations.trainUnsupervisedStartingFromRandom(observations,10,100,3,3);

        hmm.print();

    }
}
