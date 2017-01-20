package udacity.com.capstone.fragments;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by techjini on 13/1/17.
 */

public class AudioRecorder {

    final MediaRecorder recorder = new MediaRecorder();
    final String name;
    public String finalPath = "";
    public static final String IMAGE_DIRECTORY_NAME = "Recall";

    /**
     * Creates a new audio recording at the given path (relative to root of SD card).
     */
    public AudioRecorder(String name) {
        this.name = name;
    }

    private String sanitizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.contains(".")) {
            path += ".3gp";
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
    }

    /**
     * Starts a new recording.
     */
    public void start() throws IOException {
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card is not mounted.  It is " + state + ".");
        }

        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                throw new IOException("Path to file could not be created.");
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + name + ".3gp");
        finalPath = mediaFile.toURI().toString();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(mediaFile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }


    /**
     * Stops a recording that has been previously started.
     */
    public void stop() throws IOException {
        recorder.stop();
        recorder.release();
    }
}
