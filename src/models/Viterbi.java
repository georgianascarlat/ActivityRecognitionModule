package models;

public class Viterbi {

    private double delta[][];
    private int parents[][];

    public Viterbi(double[][] delta, int[][] parents) {

        int length_d = delta.length;
        int length_d0 = delta[0].length;
        this.delta = new double[length_d][length_d0];
        int length_p = parents.length;
        int length_p0 = parents[0].length;

        this.parents = new int[length_p][length_p0];

        for (int i = 0; i < length_d; i++) {
            System.arraycopy(delta[i], 0, this.delta[i], 0, length_d0);
        }

        for (int i = 0; i < length_p; i++) {
            System.arraycopy(parents[i], 0, this.parents[i], 0, length_p0);
        }

    }

    public double[][] getDelta() {
        return delta;
    }

    public int[][] getParents() {
        return parents;
    }
}
