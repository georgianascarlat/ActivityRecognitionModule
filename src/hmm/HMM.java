package hmm;

import models.Viterbi;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

import static utils.Utils.initRandomMarkovVector;

/**
 * Hidden Markov Model
 */
public abstract class HMM {

    protected int numStates, numObservableVariables;
    protected double initialStateProbabilities[];
    protected double transitionMatrix[][];
    protected double emissionMatrix[][];

    public HMM(String fileName) throws FileNotFoundException {

        loadModel(fileName);
    }

    public HMM(int numStates, int numObservableVariables) {
        this.numStates = numStates;
        this.numObservableVariables = numObservableVariables;

        this.initialStateProbabilities = new double[numStates];
        this.transitionMatrix = new double[numStates][numStates];
        this.emissionMatrix = new double[numStates][numObservableVariables];

    }

    public HMM(HMM hmm) {
        this(hmm.getNumStates(),
                hmm.getNumObservableVariables(), hmm.getInitialStateProbabilities(),
                hmm.getTransitionMatrix(), hmm.getEmissionMatrix());
    }

    protected HMM(int numStates, int numObservableVariables, double[] initialStateProbabilities, double[][] transitionMatrix, double[][] emissionMatrix) {

        this(numStates, numObservableVariables);

        System.arraycopy(initialStateProbabilities, 0, this.initialStateProbabilities, 0, numStates);

        for (int i = 0; i < numStates; i++) {
            System.arraycopy(transitionMatrix[i], 0, this.transitionMatrix[i], 0, numStates);
        }

        for (int i = 0; i < numStates; i++) {
            System.arraycopy(emissionMatrix[i], 0, this.emissionMatrix[i], 0, numObservableVariables);
        }


    }

    public abstract double[][] forward(int[] observations);

    public abstract double[][] forwardNormalized(int[] observations, double norms[]);

    public abstract double[][] backward(int[] observations);

    public abstract double[][] backwardNormalized(int[] observations, double[] norms);

    public abstract double epsilon(int t, int i, int j, int[] observations, double[][] forward, double[][] backward);

    public abstract double epsilonNormalized(int t, int i, int j,
                                             int[] observations, double gamma, double[][] backward, double[] norms);

    public abstract double gamma(int i, int t, int[] observations, double[][] forward, double[][] backward);


    public abstract double logObservationsProbability(int[] observations);

    public abstract double logObservationsProbability(List<List<Integer>> observations);


    public abstract Viterbi viterbi(int[] observations);


    public static double safeDivide(double n, double d) {
        if (n == 0 || d == 0) {
            return 0;
        } else
            return n / d;
    }

    /**
     * Randomly initialises the probabilities for the HMM.
     */
    public void randomInit() {

        initRandomMarkovVector(initialStateProbabilities);

        for (int j = 0; j < numStates; j++) {
            initRandomMarkovVector(transitionMatrix[j]);
            initRandomMarkovVector(emissionMatrix[j]);
        }


    }


    /**
     * Reads the hmm.HMM from a file.
     * <p/>
     * Format:
     * <p/>
     * numStates numObservableVariables
     * initialStateProbabilities (1 X numStates )
     * transitionMatrix  (numStates X numStates )
     * emissionMatrix   (numStates X numObservableVariables )
     *
     * @param fileName file name
     */
    public void loadModel(String fileName) throws FileNotFoundException {


        Scanner scanner = new Scanner(new File(fileName));

        numStates = scanner.nextInt();
        numObservableVariables = scanner.nextInt();

        initialStateProbabilities = new double[numStates];
        transitionMatrix = new double[numStates][numStates];
        emissionMatrix = new double[numStates][numObservableVariables];

        for (int i = 0; i < numStates; i++) {
            initialStateProbabilities[i] = scanner.nextDouble();
        }

        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numStates; j++) {
                transitionMatrix[i][j] = scanner.nextDouble();
            }
        }

        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numObservableVariables; j++) {
                emissionMatrix[i][j] = scanner.nextDouble();
            }
        }

        scanner.close();

    }

    /**
     * Writes the hmm.HMM to a file.
     * <p/>
     * Format:
     * <p/>
     * numStates numObservableVariables
     * initialStateProbabilities (1 X numStates )
     * transitionMatrix  (numStates X numStates )
     * emissionMatrix   (numStates X numObservableVariables )
     *
     * @param fileName file name
     */
    public void saveModel(String fileName) throws IOException {

        BufferedWriter out = null;

        try {

            out = new BufferedWriter(new FileWriter(fileName));


            out.write(numStates + " " + numObservableVariables + "\n");


            for (int i = 0; i < numStates; i++) {

                out.write(initialStateProbabilities[i] + " ");
            }
            out.write("\n");

            for (int i = 0; i < numStates; i++) {
                for (int j = 0; j < numStates; j++) {
                    out.write(transitionMatrix[i][j] + " ");
                }
                out.write("\n");
            }

            for (int i = 0; i < numStates; i++) {
                for (int j = 0; j < numObservableVariables; j++) {
                    out.write(emissionMatrix[i][j] + " ");
                }
                out.write("\n");
            }

        } finally {
            if (null != out)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }


    public void print() {
        DecimalFormat fmt = new DecimalFormat();
        fmt.setMinimumFractionDigits(5);
        fmt.setMaximumFractionDigits(5);

        for (int i = 0; i < numStates; i++)
            System.out.println("initialStateProbabilities(" + i + ") = " + fmt.format(initialStateProbabilities[i]));
        System.out.println();

        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numStates; j++)
                System.out.print("transitionMatrix(" + i + "," + j + ") = " +
                        fmt.format(transitionMatrix[i][j]) + "  ");
            System.out.println();
        }

        System.out.println();
        for (int i = 0; i < numStates; i++) {
            for (int k = 0; k < numObservableVariables; k++)
                System.out.print("emissionMatrix(" + i + "," + k + ") = " +
                        fmt.format(emissionMatrix[i][k]) + "  ");
            System.out.println();
        }
    }


    public int getNumStates() {
        return numStates;
    }

    public int getNumObservableVariables() {
        return numObservableVariables;
    }

    public double[] getInitialStateProbabilities() {
        return initialStateProbabilities;
    }

    public double[][] getTransitionMatrix() {
        return transitionMatrix;
    }

    public double[][] getEmissionMatrix() {
        return emissionMatrix;
    }

    protected void setInitialStateProbabilities(double[] initialStateProbabilities) {
        this.initialStateProbabilities = initialStateProbabilities;
    }

    protected void setTransitionMatrix(double[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }

    protected void setEmissionMatrix(double[][] emissionMatrix) {
        this.emissionMatrix = emissionMatrix;
    }


    public static Double safeLog(double v) {
        if (v == 0)
            return 0.0;
        return Math.log(v);
    }
}
