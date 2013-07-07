package app.activity_recognition;

import models.Activity;
import models.HMMTypes;
import utils.FileNameComparator;
import utils.Pair;
import utils.Utils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ProcessPostureFileBothHMMTypes extends ProcessPostureFile {

    private Pair<Integer, Double> lastPrediction = null;

    @Override
    public Pair<Integer, Double> processPostureFile(String postureFileName) throws IOException {


        Pair<Integer, Double> prediction1, prediction2, bestPrediction;
        final ExecutorService service;
        final Future<Pair<Integer, Double>> task1, task2;

        setHumanHeight(Utils.getSkeletonFile(postureFileName));

        service = Executors.newFixedThreadPool(5);
        task1 = service.submit(new ProcessFileOnSeparateThread(HMMTypes.GeneralHMM, postureFileName));
        task2 = service.submit(new ProcessFileOnSeparateThread(HMMTypes.SpecialisedHMM, postureFileName));

        try {


            prediction2 = task2.get();

            prediction1 = task1.get();


            service.shutdownNow();

            bestPrediction = prediction2;

            if (lastPrediction != null) {

                bestPrediction = getBestPrediction(prediction1, prediction2, lastPrediction);
            }

            lastPrediction = bestPrediction;


            return bestPrediction;

        } catch (final InterruptedException ex) {
            ex.printStackTrace();

        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        throw new RuntimeException("Can't finish Thread work");

    }

    private Pair<Integer, Double> getBestPrediction(Pair<Integer, Double> prediction1, Pair<Integer, Double> prediction2, Pair<Integer, Double> lastPrediction) {

        int activity1 = prediction1.getFirst();
        int activity2 = prediction2.getFirst();
        int lastActivity = lastPrediction.getFirst();


        if (activity1 == activity2)
            return prediction1;

        if (probableTransition(lastActivity, activity2)) {

            return prediction2;
        }


        if (probableTransition(lastActivity, activity1)) {

            return prediction1;
        }


        return prediction2;
    }

    private boolean probableTransition(int lastActivity, int currentActivity) {

        Activity activity1, activity2;

        if (lastActivity == currentActivity)
            return true;

        if (lastActivity == 0 || currentActivity == 0)
            return false;

        activity1 = Activity.getActivityByIndex(lastActivity);
        activity2 = Activity.getActivityByIndex(currentActivity);

        return Activity.SittingDown == activity1 && Activity.StandingUp == activity2 || Activity.Falling == activity1 && Activity.LyingDown == activity2;


    }
}
