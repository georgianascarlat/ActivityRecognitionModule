import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        HMMOperations hmmOperations = new HMMOperationsImpl();
        int observations[] = {1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1};
        int newObservations[] = {2, 2, 1, 0, 1, 3, 2, 0, 0};

        HMM hmm = new HMMCalculus("HMMinput2.txt");

        hmm.print();


//        hmm = hmmOperations.train(observations, 1000, hmm);
//
//        hmm.print();
//
//        hmm.saveModel("HMMoutput.txt");

        Prediction prediction = hmmOperations.predict(hmm, newObservations);

        System.out.println(prediction);


    }
}
