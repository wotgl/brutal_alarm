package nazvaniye.nado.brutalalarm;

import android.app.Activity;
import android.widget.ListView;

public class MainMenuListManager {
    public MainMenuListManager(Activity activity) {
        ListView alarmList = (ListView)activity.findViewById(R.id.alarmsList);
        Adapter adapter = new Adapter(activity);
        alarmList.setAdapter(adapter);
    }
}
