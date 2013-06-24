package app;

import models.Posture;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.Utils.getTrainPostures;

public class InputTransformation {

    public static void main(String args[]) throws IOException {

        List<List<Posture>> postures = getTrainPostures();
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("total_data.txt", true)));

        for (List<Posture> seq : postures) {
            for (Posture posture : seq) {
                out.println(posture.getGeneralPosture() + "," + posture.getTorsoFirst() + "," + posture.getTorsoSecond() + "," +
                        posture.getHead() + "," + posture.getLeftHandFirst() + "," + posture.getRightHandFirst() + "," + posture.getLeftHandSecond()
                        + "," + posture.getRightHandSecond() + "," + posture.getLeftLegFirst() + "," + posture.getRightLegFirst() + "," +
                        posture.getLeftLegSecond() + "," + posture.getRightLegSecond() + "," + posture.getActivity());
            }
        }

        out.close();
    }
}
