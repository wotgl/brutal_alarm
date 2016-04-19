package nazvaniye.nado.brutalalarm.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import nazvaniye.nado.brutalalarm.Game;
import nazvaniye.nado.brutalalarm.R;
import nazvaniye.nado.brutalalarm.services.CrazyService;
import nazvaniye.nado.brutalalarm.services.RecoverAlarmService;

public class AlarmActivity extends AppCompatActivity {
    private Context context = this;

    private int difficulty;
    private long time;

    private boolean activityCanBeReleased = false;
    private boolean humanSurvived = false;

    private CountDownTimer timer;
    private Vibrator vibrator;
    private Game game;

    private double WIN_SCORES[] = {2000, 10000, 35000, 100000};

    private MediaPlayer player;
    private AudioManager am;
    private int volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.alarm);

        final TextView timeTextView = (TextView)findViewById(R.id.timeTextView);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);

        progressBar.setProgress(progressBar.getMax());

        long minutes = time / 1000 / 60;
        long seconds = time / 1000 % 60;
        timeTextView.setText(String.format("%02d:%02d", minutes, seconds));

        Intent intent = getIntent();
        difficulty = intent.getIntExtra("difficulty", -1);
        time = intent.getLongExtra("time", -1);

        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                vibrator.vibrate(100);
                time = millisUntilFinished;
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = millisUntilFinished / 1000 % 60;

                if(!humanSurvived && game.getScore() >= WIN_SCORES[difficulty]) {
                    humanSurvived = true;
                    activityCanBeReleased = true;
                    timeTextView.setText(R.string.survived);
                    ((TextView)findViewById(R.id.infoTextView)).setText(R.string.press_home_button);
                    findViewById(R.id.alarmLayout).setBackgroundColor(Color.GREEN);
                }

                if(!humanSurvived) {
                    timeTextView.setText(String.format("%02d:%02d", minutes, seconds));
                }
            }
            @Override
            public void onFinish() {
                activityCanBeReleased = true;
                if(!humanSurvived) {
                    Intent intent = new Intent(context, CrazyService.class);
                    context.startService(intent);
                }
                finish();
            }
        };
        timer.start();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        game = new Game(this);

        player = MediaPlayer.create(this, R.raw.slider);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setLooping(true);
        player.start();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    private void recoverSelfIfNeeded() {
        if(!activityCanBeReleased) {
            Intent intent = new Intent(this, RecoverAlarmService.class);
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("time", time);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        recoverSelfIfNeeded();
        timer.cancel();
        game.destroy();

        player.stop();
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

}
