import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        HMMOperations hmmOperations = new HMMOperationsImpl();
        int observations[] = {0,0,0,1,0,1,1,1,0,0,1,1,1,1};

        HMM hmm = new HMMCalculus("HMMinput.txt");

        hmm.print();


        hmm = hmmOperations.train(observations,1000,hmm);

        hmm.print();

        hmm.saveModel("HMMoutput.txt");


    }
}
