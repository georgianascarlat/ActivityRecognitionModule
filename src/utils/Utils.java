package utils;


import models.Posture;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Utils {

    public static final String TRAIN_DIRECTORY = "train/";
    public static final String HMM_DIRECTORY = "learned_HMMs/";
    public static final String DATA_DIRECTORY = "data/";
    public static final String ACTIVITY_FILE = "activity/activity_recognition.txt";

    /**
     * Make sorted list of the names of all the files in a directory.
     *
     * @param directoryFiles files in directory
     * @return sorted list of the names of all the files in a directory
     */
    public static List<String> getFileNamesFromDirectory(File[] directoryFiles) {
        List<String> filesInDirectory;
        filesInDirectory = new LinkedList<String>();

        for (File subFile : directoryFiles) {
            if (subFile.isFile() && !subFile.getPath().endsWith("~")) {
                filesInDirectory.add(subFile.getPath());
            }
        }

        Collections.sort(filesInDirectory, new FileNameComparator());
        return filesInDirectory;
    }

    /**
     * Obtain a list of all the training file names sequences from the training
     * directory.
     *
     * @return list of all the training file names sequences
     */
    private static List<List<String>> getTrainingFileNames() {

        List<List<String>> fileNames = new LinkedList<List<String>>();
        List<String> filesInDirectory;

        File[] files = new File(TRAIN_DIRECTORY).listFiles(), directoryFiles;
        for (File file : files) {
            if (file.isDirectory()) {

                directoryFiles = file.listFiles();
                filesInDirectory = getFileNamesFromDirectory(directoryFiles);
                fileNames.add(filesInDirectory);

            }

        }

        return fileNames;
    }

    /**
     * Get a list of postures from a list of file names containing posture information.
     *
     * @param filesInSequence file names containing posture information
     * @return list of postures
     * @throws FileNotFoundException
     */
    public static List<Posture> getPosturesFromFileSequence(List<String> filesInSequence) throws FileNotFoundException {

        List<Posture> sequencePostures;
        sequencePostures = new LinkedList<Posture>();
        for (String fileName : filesInSequence) {
            sequencePostures.add(new Posture(fileName));
        }
        return sequencePostures;
    }


    /**
     * Read all the training files from the training directory
     * and create a list of posture objects from all of them.
     *
     * @return list of posture objects
     * @throws java.io.FileNotFoundException invalid file names
     */
    public static List<List<Posture>> getTrainPostures() throws FileNotFoundException {
        List<List<String>> fileNames = getTrainingFileNames();

        List<List<Posture>> postures = new LinkedList<List<Posture>>();
        List<Posture> sequencePostures;
        for (List<String> filesInSequence : fileNames) {
            sequencePostures = getPosturesFromFileSequence(filesInSequence);
            postures.add(sequencePostures);

        }

        return postures;
    }
}
