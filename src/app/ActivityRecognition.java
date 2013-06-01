package app;


import models.Posture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static utils.Utils.DATA_DIRECTORY;

public class ActivityRecognition {



    public static void main(String args[]) throws IOException, URISyntaxException {

        new ActivityRecognition().waitForNewFiles();

    }

    public void waitForNewFiles() throws IOException {

        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(DATA_DIRECTORY);

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
                        System.err.format("New file '%s'" +
                                " is not a plain text file.%n", filename);
                        continue;
                    }


                } catch (IOException x) {
                    System.err.println(x);
                    continue;
                }


                /* Verify file name */
                if (!filename.toString().startsWith("posture_") || !filename.toString().endsWith(".txt")) {
                    continue;
                }


                try {
                    processNewFile(DATA_DIRECTORY+filename.toString());
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

    private void processNewFile(String filename) throws FileNotFoundException {

        Posture posture = new Posture(filename);

        System.out.println(posture);


    }


}
