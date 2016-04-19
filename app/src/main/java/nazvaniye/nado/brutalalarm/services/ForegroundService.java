package nazvaniye.nado.brutalalarm.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nazvaniye.nado.brutalalarm.Data;
import nazvaniye.nado.brutalalarm.R;
import nazvaniye.nado.brutalalarm.activities.MainActivity;

public class ForegroundService extends Service {

    private boolean started = false;

    private AlarmTask currentTask = null;
    private boolean oneTimeAlarm = false;
    private Data.AlarmTable currentAlarm;

    private android.support.v4.app.NotificationCompat.Builder notificationBuilder;
    private final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        setup(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!started) {
            started = true;
        } else {
            setup(false);
        }

        return START_STICKY;
    }

    private void setup(boolean isStart) {
        if(currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }

        Calendar time = getAlarmTime();
        if(time != null) {
            if(isStart) {
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(getString(R.string.alarm_is_running).toUpperCase())
                        .setContentIntent(pendingIntent);
                Notification notification = notificationBuilder.build();

                startForeground(NOTIFICATION_ID, notification);
            }

            long difference = time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
            currentTask = new AlarmTask();
            currentTask.execute(difference, null, null);
        } else {
            stopSelf();
        }
    }

    private class AlarmTask extends AsyncTask<Long, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Long... data) {
            final long remained = data[0];
            final long startTime = System.currentTimeMillis();

            updateNotification(remained);
            try {
                Thread.sleep(remained % 60000);

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        updateNotification(remained - (System.currentTimeMillis() - startTime));
                    }
                }, 0, 60000);

                try {
                    Thread.sleep(remained - remained % 60000);
                } catch (InterruptedException e) {
                    timer.cancel();
                    return true;
                }
                timer.cancel();
            } catch (InterruptedException e) {
                return true;
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }

        @Override
        protected void onPostExecute(final Boolean interrupted) {
            if(!interrupted) {
                if(oneTimeAlarm) {
                    currentAlarm.enabled = false;
                    currentAlarm.save();
                }
                showAlarm();
            }
            setup(false);
        }
    }

    private Calendar getAlarmTime() {
        Calendar now = Calendar.getInstance();
        Calendar earliest = null;

        Iterator<Data.AlarmTable> alarms = Data.AlarmTable.findAll(Data.AlarmTable.class);
        while(alarms.hasNext()) {
            Data.AlarmTable alarm = alarms.next();
            if(alarm.enabled) {
                final int[] DAYS = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

                ArrayList<Integer> days = alarm.getDaysOfWeek();
                if(days.size() > 0) {
                    for (int i = 0; i < days.size(); ++i) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, alarm.hour);
                        c.set(Calendar.MINUTE, alarm.minute);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.MILLISECOND, 0);

                        int day = DAYS[days.get(i)];
                        c.set(Calendar.DAY_OF_WEEK, day);
                        if (c.before(now)) {
                            c.add(Calendar.WEEK_OF_YEAR, 1);
                        }

                        if (earliest == null || c.before(earliest)) {
                            earliest = c;

                            currentAlarm = alarm;
                            oneTimeAlarm = false;
                        }
                    }
                } else {
                    earliest = Calendar.getInstance();
                    earliest.set(Calendar.HOUR_OF_DAY, alarm.hour);
                    earliest.set(Calendar.MINUTE, alarm.minute);
                    earliest.set(Calendar.SECOND, 0);
                    earliest.set(Calendar.MILLISECOND, 0);

                    if (earliest.before(now)) {
                        earliest.add(Calendar.DAY_OF_WEEK, 1);
                    }

                    currentAlarm = alarm;
                    oneTimeAlarm = true;
                }
            }
        }

        return earliest;
    }

    private void updateNotification(final long difference) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        long days = TimeUnit.MILLISECONDS.toDays(difference);
        long hours = TimeUnit.MILLISECONDS.toHours(difference) - days * 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference) - days * 24 * 60 - hours * 60;

        String msg;
        if(days > 0) {
            msg = (days + 1) + " " + getString(R.string.day);
        } else if (hours > 0) {
            msg = hours + ":" + (minutes + 1);
        } else {
            msg = (minutes + 1) + " " + getString(R.string.minute);
        }

        notificationBuilder.setContentText(msg);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void showAlarm() {
        Intent intent = new Intent("android.intent.category.LAUNCHER");
        intent.setClassName(
                "nazvaniye.nado.brutalalarm",
                "nazvaniye.nado.brutalalarm.activities.AlarmActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("difficulty", currentAlarm.difficulty);
        intent.putExtra("time", (long)getResources().getInteger(R.integer.alarm_time));

        startActivity(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
