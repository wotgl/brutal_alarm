package nazvaniye.nado.brutalalarm;

import com.vk.sdk.VKSdk;

public class MainApplication extends com.orm.SugarApp  {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}