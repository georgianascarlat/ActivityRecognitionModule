package app;

import models.Posture;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Posture posture = new Posture("train/sequence1/posture_1.txt");
        System.out.println(posture);

        List<String> list = new LinkedList<String>();
        list.add("generalPosture");
        list.add("torsoFirst");
        list.add("torsoSecond");

        System.out.println(Posture.computeNumObservableVariables(list));

        System.out.println(posture.computeObservationIndex(list));


    }
}
