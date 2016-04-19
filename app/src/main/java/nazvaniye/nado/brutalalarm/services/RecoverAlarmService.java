package nazvaniye.nado.brutalalarm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecoverAlarmService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent activityIntent = new Intent("android.intent.category.LAUNCHER");
        activityIntent.setClassName(
                "nazvaniye.nado.brutalalarm",
                "nazvaniye.nado.brutalalarm.activities.AlarmActivity");
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activityIntent.putExtra("difficulty", intent.getIntExtra("difficulty", -1));
        activityIntent.putExtra("time", intent.getLongExtra("time", -1));

        startActivity(activityIntent);
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
