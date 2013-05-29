import java.util.Arrays;

public class Prediction {

    private int[] observations;
    private int[] predictions;
    private double probability;

    public Prediction(int[] observations, int[] predictions, double probability) {


        int length = observations.length;
        if (length != predictions.length)
            throw new IllegalArgumentException("Observations and predictions must have the same size");

        this.observations = new int[length];
        this.predictions = new int[length];

        System.arraycopy(observations, 0, this.observations, 0, length);

        System.arraycopy(predictions, 0, this.predictions, 0, length);

        this.probability = probability;
    }

    public int[] getObservations() {
        return observations;
    }

    public int[] getPredictions() {
        return predictions;
    }

    public double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "observations=" + Arrays.toString(observations) +
                ", predictions=" + Arrays.toString(predictions) +
                ", probability=" + probability +
                '}';
    }
}
