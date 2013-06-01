package app;


import hmm.HMM;
import hmm.HMMOperations;
import hmm.HMMOperationsImpl;
import models.Activity;
import models.Posture;
import utils.FileNameComparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
     *
     * @param activity  activity for which to create the HMM
     * @param postures  list of sequences of postures to be used as training set
     */
    private static void createActivityHMM(Activity activity, List<List<Posture>>postures) throws IOException {

        List<String> posturesOfInterest = activityMap.get(activity);
        int numObservableVariables = Posture.computeNumObservableVariables(posturesOfInterest);
        int numStates = 2, numSequences = postures.size(), sequenceLength = postures.get(0).size();
        HMMOperations hmmOperations = new HMMOperationsImpl();
        HMM hmm;
        List<List<Integer>> observations = new ArrayList<List<Integer>>(),
                hiddenStates = new ArrayList<List<Integer>>();
        List<Integer> aux_o,aux_s;



        /* transform posture information into observable variables and hidden states*/
        for(List<Posture> sequence:postures){

            aux_o = new ArrayList<Integer>();
            aux_s = new ArrayList<Integer>();

            for(Posture posture:sequence){

                /* transform posture information into observation index*/
                aux_o.add(posture.computeObservationIndex(posturesOfInterest));

                /* get the tagged activity, state 1 means the activity is detected, 0 otherwise*/
                aux_s.add(posture.getActivity() == activity.getIndex()?1:0);
            }
            observations.add(aux_o);
            hiddenStates.add(aux_s);
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
                    if(subFile.isFile() && !subFile.getPath().endsWith("~")){
                        filesInDirectory.add(subFile.getPath());
                    }
                }

                Collections.sort(filesInDirectory, new FileNameComparator());
                fileNames.add(filesInDirectory);

            }

        }

        return fileNames;
    }
}
