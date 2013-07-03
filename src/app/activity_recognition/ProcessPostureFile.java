package app.activity_recognition;


import models.HMMTypes;
import utils.Pair;

import java.io.IOException;


public abstract class ProcessPostureFile {


    public abstract Pair<Integer, Double> processPostureFile(String postureFileName) throws IOException;


    public static ProcessPostureFile factory(HMMTypes hmmType) {
        switch (hmmType) {

            case SpecialisedHMM:

                return new ProcessPostureFileSpecificHMM();


            case GeneralHMM:

                return new ProcessPostureFileGeneralHMM();

            case BothHMMTypes:

                return new ProcessPostureFileBothHMMTypes();

            default:

                throw new IllegalArgumentException("No such HMM Type " + hmmType);
        }
    }
}
