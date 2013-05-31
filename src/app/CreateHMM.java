package app;


import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Posture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreateHMM {

    public static final String TRAIN_DIRECTORY = "train/";
    public static final String HMM_DIRECTORY = "learned_HMMs/";

    private static final Map<Activity, List<String>> activityMap = initActivitiesMap();

    /**
     * Initialise the mapping from activities to their list of postures of interest.
     *
     * @return mapping from activities to their list of postures of interest
     */
    private static Map<Activity, List<String>> initActivitiesMap() {
        Map<Activity,List<String>> map = new EnumMap<Activity, List<String>>(Activity.class);
        List<String> interestingPostures;

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftLegFirst");
        interestingPostures.add("rightLegFirst");
        interestingPostures.add("leftLegSecond");
        interestingPostures.add("rightLegSecond");
        map.put(Activity.Walking,interestingPostures);

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        map.put(Activity.LyingDown,interestingPostures);

        return map;


    }


    public static void main(String args[]) throws IOException {

        /*read the posture information from training files*/
        List<List<Posture>> postures = getTrainPostures();


        /* create a HMM for each activity*/
        for(Activity activity:Activity.values()){
            createActivityHMM(activity,postures);
        }

    }

    /**
     * Creates a HMM for a given activity using the observations deduced
     * from the list of postures.
     *
     * Attention: The sequences in the list must all have the same size!
     *
     * @param activity  activity for which to create the HMM
     * @param postures  list of sequences of postures to be used as training set
     */
    private static void createActivityHMM(Activity activity, List<List<Posture>>postures) throws IOException {

        List<String> posturesOfInterest = activityMap.get(activity);
        int numObservableVariables = Posture.computeNumObservableVariables(posturesOfInterest);
        int numStates = 2, numSequences = postures.size(), sequenceLength = postures.get(0).size();
        int[][] observations = new int[numSequences][sequenceLength];
        int[][] hiddenStates = new int[numSequences][sequenceLength];
        HMMOperations hmmOperations = new HMMOperationsImpl();
        HMM hmm;



        /* transform posture information into observable variables and hidden states*/
        for(int s = 0;s<numSequences;s++){
            for(int i=0;i<sequenceLength;i++){

                /* transform posture information into observation index*/
                observations[s][i] = postures.get(s).get(i).computeObservationIndex(posturesOfInterest);
                /* get the tagged activity, state 1 means the activity is detected, 0 otherwise*/
                hiddenStates[s][i] = postures.get(s).get(i).getActivity() == activity.getIndex()?1:0;

            }
            //System.out.println();
        }

        /* train the model using the observations and hidden states*/
        hmm =  hmmOperations.trainSupervised(numStates,numObservableVariables,observations,hiddenStates);

        /*save the model into it's corresponding file*/
        hmm.saveModel(HMM_DIRECTORY+activity.getName()+".txt");

    }

    /**
     *
     * Read all the training files from the training directory
     * and create a list of posture objects from all of them.
     *
     *
     * @return  list of posture objects
     *
     * @throws FileNotFoundException  invalid file names
     */
    private static List<List<Posture>> getTrainPostures() throws FileNotFoundException {
        List<List<String>> fileNames =  getTrainingFileNames();

        List<List<Posture>> postures = new LinkedList<List<Posture>>();
        List<Posture> sequencePostures;
        for(List<String> filesInSequence:fileNames){
            sequencePostures = new LinkedList<Posture>();
            for(String fileName:filesInSequence)  {
                sequencePostures.add(new Posture(fileName));
            }
            postures.add(sequencePostures);

        }

        return postures;
    }


    /**
     * Obtain a list of all the training file names sequences from the training
     * directory.
     *
     * @return  list of all the training file names sequences
     */
    private static List<List<String>> getTrainingFileNames(){

        List<List<String>> fileNames = new LinkedList<List<String>>();
        List<String> filesInDirectory;

        File[] files = new File(TRAIN_DIRECTORY).listFiles(), directoryFiles;
        for(File file:files){
            if(file.isDirectory()){
                filesInDirectory = new LinkedList<String>();
                directoryFiles = file.listFiles();
                for(File subFile:directoryFiles){
                    if(subFile.isFile() && !subFile.getAbsolutePath().endsWith("~")){
                        filesInDirectory.add(subFile.getAbsolutePath());
                    }
                }

                fileNames.add(filesInDirectory);

            }

        }

        return fileNames;
    }
}
