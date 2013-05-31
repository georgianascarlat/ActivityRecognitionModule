import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class HMMOperationsTest {


    private HMM hmm1, hmm2, hmm3;
    private HMMOperations hmmOperations;
    public static final double EPSILON = 0.01;

    @Before
    public void setUp() throws Exception {

        hmmOperations = new HMMOperationsImpl();
        hmm1 = new HMMCalculus("HMMinput2.txt");
        hmm2 = new HMMCalculus("HMMinput3.txt");
        hmm3 = new HMMCalculus("HMMinput4.txt");

    }

    @Test
    public void testLearnSupervised(){

        int observations[][] = {{0,0,1,0,1,0},
                                {1,0,1,0,1,0},
                                {1,0,0,1,1,0},
                                {1,0,1,1,1,0},
                                {1,0,0,1,0,1},
                                {0,0,1,0,0,1},
                                {0,0,1,1,0,1},
                                {0,1,1,1,0,0}};
        int states[][] = {{0,0,0,1,0,0},
                {1,0,0,1,0,0},
                {0,0,1,0,0,0},
                {0,0,0,0,1,0},
                {1,0,0,0,1,0},
                {0,0,0,1,1,0},
                {1,0,0,0,0,0},
                {1,0,1,0,0,0}};
        double expectedInitialProbabilities[] = {0.5,0.5};
        double expectedTransitionMatrix[][] = {{3.0/4,1.0/4},{11.0/12,1.0/12}};
        double expectedEmissionMatrix[][] = {{17.0/36,19.0/36},{2.0/3,1.0/3}};
        HMM hmm = hmmOperations.trainSupervised(2,2,observations,states);


        assertArrayEquals(expectedInitialProbabilities,hmm.getInitialStateProbabilities(),EPSILON);
        for(int i=0;i<2;i++){
            assertArrayEquals(expectedTransitionMatrix[i],hmm.getTransitionMatrix()[i],EPSILON);
            assertArrayEquals(expectedEmissionMatrix[i],hmm.getEmissionMatrix()[i],EPSILON);
        }

    }


    @Test
    public void testPredict1() throws Exception {

        int observations[] = {2, 2, 1, 0, 1, 3, 2, 0, 0};
        int expectedPrediction[] = {0, 0, 0, 1, 1, 1, 1, 1, 1};
        double expectedProbability = 4.25E-8;

        Prediction prediction = hmmOperations.predict(hmm1, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());

        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);


    }

    @Test
    public void testPredict2() throws Exception {


        int observations[] = {0, 0, 1, 0, 1, 0};
        int expectedPrediction[] = {1, 1, 0, 1, 0, 1};


        Prediction prediction = hmmOperations.predict(hmm2, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());

    }

    @Test
    public void testPredict3() throws Exception {


        int observations[] = {1, 0, 1, 0, 1, 0};
        int expectedPrediction[] = {0, 1, 0, 1, 0, 1};


        Prediction prediction = hmmOperations.predict(hmm2, observations);


        assertArrayEquals(expectedPrediction, prediction.getPredictions());

    }

    @Test
    public void testPredict4() throws Exception {


        int observations[] = {1, 1, 0, 1, 1, 0};
        int expectedPrediction[] = {0, 0, 1, 0, 0, 1};


        Prediction prediction = hmmOperations.predict(hmm2, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());

    }

    @Test
    public void testPredict5() throws Exception {


        int observations[] = {0, 2, 3};
        int expectedPrediction[] = {0, 2, 2};
        double expectedProbability = 6.2E-3;


        Prediction prediction = hmmOperations.predict(hmm3, observations);


        assertArrayEquals(expectedPrediction, prediction.getPredictions());
        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);

    }

    @Test
    public void testPredict6() throws Exception {


        int observations[] = {3, 1, 1, 2, 0};
        int expectedPrediction[] = {2, 1, 0, 0, 0};
        double expectedProbability = 2.847E-5;


        Prediction prediction = hmmOperations.predict(hmm3, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());
        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);

    }

    @Test
    public void testPredict7() throws Exception {


        int observations[] = {2, 3, 3, 0};
        int expectedPrediction[] = {2, 2, 1, 0};
        double expectedProbability = 4.983E-4;


        Prediction prediction = hmmOperations.predict(hmm3, observations);

        assertArrayEquals(expectedPrediction, prediction.getPredictions());
        assertTrue(Math.abs(expectedProbability - prediction.getProbability()) < EPSILON);

    }
}
