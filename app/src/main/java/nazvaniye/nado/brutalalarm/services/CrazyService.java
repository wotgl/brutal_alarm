package nazvaniye.nado.brutalalarm.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nazvaniye.nado.brutalalarm.Game;
import nazvaniye.nado.brutalalarm.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrazyService extends Service {
    private Context context = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        callPeople(this);
        sendMessage();
        stopSelf();

        return START_NOT_STICKY;
    }

    private void callPeople(final Context context) {
        final Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
        final int len = phones.getCount();
        final Random rnd = new Random();

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int i = 0;
            private long time = 0;

            @Override
            public void run() {
                time += 100;
                if ((time > 60000 && i >= 13) || !phones.moveToPosition(rnd.nextInt(len))) {
                    cancel();
                    phones.close();
                } else if (!isCallActive()) {
                    i++;
                    String phoneNumber = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                }
            }
        }, 0, 100);
    }

    private boolean isCallActive(){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }

    private void sendVideo() {
        if(VKSdk.isLoggedIn()) {
            VKParameters params = new VKParameters();
            params.put("name", "I am sleeping");
            params.put("description", "ZzzZZzzzZZzz...");
            params.put("wallpost", 1);

            VKApi.video().save(params).executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    try {
                        String uploadUrl = (String) response.json.getJSONObject("response").get("upload_url");

                        OkHttpClient client = new OkHttpClient();

                        RequestBody body = RequestBody.create(
                                MediaType.parse("video/mp4"),
                                new File(Game.getLastFilename(context)));

                        final Request request = new Request.Builder()
                                .url(uploadUrl)
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject json = new JSONObject(response.body().string());
                                    Log.e("brutal", response.body().string());
                                } catch (JSONException e) {
                                    Log.e("brutal", "suka");
                                }
                            }
                        });

                    } catch (JSONException ignore) {
                    }
                }
            });
        }
    }

    private void sendMessage() {
        if(VKSdk.isLoggedIn()) {
            VKParameters params = new VKParameters();
            params.put("message", getString(R.string.vk_message));
            VKApi.wall().post(params).executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
