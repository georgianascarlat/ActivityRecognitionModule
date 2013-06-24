package app.activity_recognition;

import utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static utils.Utils.DATA_DIRECTORY;
import static utils.Utils.READY_PREFIX;

public class ActivityRecognitionOnline extends ActivityRecognition {

    public ActivityRecognitionOnline() throws FileNotFoundException {
        super();
    }

    @Override
    public void activityRecognition() throws IOException {

        waitForNewFiles();
    }


    /**
     * Waits for new posture files to be added in the directory DATA_DIRECTORY.
     * <p/>
     * When a new posture file is created, it processes it by predicting
     * the corresponding activity, printing and logging it into a file.
     *
     * @throws java.io.IOException
     */
    public void waitForNewFiles() throws IOException {

        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(DATA_DIRECTORY);
        String readyFileName;

        dir.register(watcher, ENTRY_CREATE);

        for (; ; ) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();


                if (kind == OVERFLOW) {
                    continue;
                }


                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                /* Verify that the new file is a text file.*/
                try {

                    Path child = dir.resolve(filename);
                    if (!Files.probeContentType(child).equals("text/plain")) {

                        continue;
                    }


                } catch (IOException x) {
                    System.err.println(x);
                    continue;
                }


                /* Verify file name */
                if (!filename.toString().startsWith(READY_PREFIX) || !filename.toString().endsWith(".txt")) {
                    continue;
                }


                try {
                    readyFileName = DATA_DIRECTORY + filename.toString();
                    processNewFile(Utils.getPostureFile(readyFileName));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            /* reset the key*/
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

}
