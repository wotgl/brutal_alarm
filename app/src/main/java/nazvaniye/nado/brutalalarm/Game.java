package nazvaniye.nado.brutalalarm;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;

import java.io.IOException;

public class Game implements SensorEventListener {
    Activity activity;

    private double score = 0;

    private MediaRecorder recorder;
    private SensorManager sensorManager;

    public static String getLastFilename(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/last.mp3";
    }

    public Game(Activity activity) {
        this.activity = activity;

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(getLastFilename(activity));
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IllegalStateException | IOException e) {
            throw new NullPointerException();
        }
        recorder.start();

        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public double getScore() {
        return score;
    }

    public void destroy() {
        recorder.stop();
        recorder.reset();
        recorder.release();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double strength = Math.sqrt((double)
                (event.values[0]*event.values[0]+
                 event.values[1]*event.values[1]+
                 event.values[2]*event.values[2])
        );

        strength = Math.abs(strength - 10);
        if(strength < 3) strength = 0;

        int volume = recorder.getMaxAmplitude() / 1000;
        double finalScore = strength * volume;
        score += finalScore;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
