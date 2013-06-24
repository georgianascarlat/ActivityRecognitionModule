package app.activity_recognition;


import models.HMMTypes;
import models.Posture;
import utils.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public abstract class ProcessPostureFile {


    public abstract void processPostureFile(String postureFileName) throws IOException;

    protected void appendActivityToFile(int frameNumber, int predictedActivityIndex, double probability, Posture posture) throws IOException {

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Utils.ACTIVITY_FILE, true)));

        if (posture.getActivity() >= 0)
            out.println(frameNumber + "," + predictedActivityIndex + "," + probability + "," + posture.getActivity() + ",");
        else
            out.println(frameNumber + "," + predictedActivityIndex + "," + probability + ",");
        out.close();
    }

    public static ProcessPostureFile factory(HMMTypes hmmType) {
        switch (hmmType) {

            case SingleLayerHMM:

                return new ProcessPostureFileSingleLayerHMM();

            case TwoLayerHMM:

                return new ProcessPostureFileTwoLayerHMM();

            case GeneralHMM:

                return new ProcessPostureFileGeneralHMM();

            default:

                throw new IllegalArgumentException("No such HMM Type " + hmmType);
        }
    }
}
