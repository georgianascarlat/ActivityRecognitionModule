import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        HMMOperations hmmOperations = new HMMOperationsImpl();
        int observations[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        System.out.println("\nInitial: ");
        HMM hmm = new HMMCalculus("HMMinput_0.txt");

        hmm.print();


        System.out.println("\nTrain: ");
        hmm = hmmOperations.trainUnsupervised(observations, 1000, hmm);

        hmm.print();




        hmm.saveModel("HMMoutput.txt");





    }
}
