package app.activity_recognition;

import utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


public class ActivityRecognitionOffline extends ActivityRecognition {

    public static final String DIRECTORY_NAME = "from_ema/RealTime/DONE/MaiMulte1";


    public ActivityRecognitionOffline() throws FileNotFoundException {
        super();
    }

    @Override
    public void activityRecognition() throws IOException {
        processFilesInDirectory(DIRECTORY_NAME);
    }


    private void processFilesInDirectory(String directoryName) throws IOException {

        File directory = new File(directoryName);


        List<String> postureFileNames = Utils.getFileNamesFromDirectory(directory.listFiles());


        for (String fileName : postureFileNames) {
            processNewFile(fileName);
        }


    }
}
