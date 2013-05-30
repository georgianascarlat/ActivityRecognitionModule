import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class HMMOperationsTest {


    private HMM hmm1, hmm2, hmm3;
    private HMMOperations hmmOperations;
    public static final double EPSILON = 0.01;

    @org.junit.Before
    public void setUp() throws Exception {

        hmmOperations = new HMMOperationsImpl();
        hmm1 = new HMMCalculus("HMMinput2.txt");
        hmm2 = new HMMCalculus("HMMinput3.txt");
        hmm3 = new HMMCalculus("HMMinput4.txt");

    }


    @org.junit.Test
    public void testPredict1() throws Exception {

        int observations[] = {2, 2, 1, 0, 1, 3, 2, 0, 0};
        int expectedPrediction[] = {0, 0, 0, 1, 1, 1, 1, 1, 1};
        double expectedProbability = 4.25E-8;

        Prediction prediction = hmmOperations.predict(hmm1, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());

        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);


    }

    @org.junit.Test
    public void testPredict2() throws Exception {


        int observations[] = {0, 0, 1, 0, 1, 0};
        int expectedPrediction[] = {1, 1, 0, 1, 0, 1};


        Prediction prediction = hmmOperations.predict(hmm2, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());

    }

    @org.junit.Test
    public void testPredict3() throws Exception {


        int observations[] = {1, 0, 1, 0, 1, 0};
        int expectedPrediction[] = {0, 1, 0, 1, 0, 1};


        Prediction prediction = hmmOperations.predict(hmm2, observations);


        assertArrayEquals(expectedPrediction, prediction.getPredictions());

    }

    @org.junit.Test
    public void testPredict4() throws Exception {


        int observations[] = {1, 1, 0, 1, 1, 0};
        int expectedPrediction[] = {0, 0, 1, 0, 0, 1};


        Prediction prediction = hmmOperations.predict(hmm2, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());

    }

    @org.junit.Test
    public void testPredict5() throws Exception {


        int observations[] = {0, 2, 3};
        int expectedPrediction[] = {0, 2, 2};
        double expectedProbability = 6.2E-3;


        Prediction prediction = hmmOperations.predict(hmm3, observations);


        assertArrayEquals(expectedPrediction, prediction.getPredictions());
        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);

    }

    @org.junit.Test
    public void testPredict6() throws Exception {


        int observations[] = {3, 1, 1, 2, 0};
        int expectedPrediction[] = {2, 1, 0, 0, 0};
        double expectedProbability = 2.847E-5;


        Prediction prediction = hmmOperations.predict(hmm3, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());
        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);

    }

    @org.junit.Test
    public void testPredict7() throws Exception {


        int observations[] = {2, 3, 3, 0};
        int expectedPrediction[] = {2, 2, 1, 0};
        double expectedProbability = 4.983E-4;


        Prediction prediction = hmmOperations.predict(hmm3, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());
        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);

    }
}
