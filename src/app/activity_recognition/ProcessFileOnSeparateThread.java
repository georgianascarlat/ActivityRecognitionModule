package app.activity_recognition;

import models.HMMTypes;
import utils.Pair;

import java.util.concurrent.Callable;

public class ProcessFileOnSeparateThread implements Callable<Pair<Integer, Double>> {

    private HMMTypes hmmType;
    private String postureFile;

    public ProcessFileOnSeparateThread(HMMTypes hmmType, String postureFile) {
        this.hmmType = hmmType;
        this.postureFile = postureFile;
    }

    @Override
    public Pair<Integer, Double> call() throws Exception {

        return ProcessPostureFile.factory(hmmType).processPostureFile(postureFile);

    }
}
