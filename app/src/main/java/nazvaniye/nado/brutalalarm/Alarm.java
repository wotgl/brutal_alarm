package nazvaniye.nado.brutalalarm;


import android.content.Context;
import android.content.Intent;

import nazvaniye.nado.brutalalarm.services.ForegroundService;

public class Alarm {
    public static void cheerUp(Context context) {
        Intent intent = new Intent(context, ForegroundService.class);
        context.startService(intent);
    }
}
