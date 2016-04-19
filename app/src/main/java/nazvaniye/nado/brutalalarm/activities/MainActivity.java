package nazvaniye.nado.brutalalarm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import nazvaniye.nado.brutalalarm.BaseActivity;
import nazvaniye.nado.brutalalarm.MainMenuListManager;
import nazvaniye.nado.brutalalarm.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);

        new MainMenuListManager(this);
    }

    public void addButtonClick(View v) {
        Intent intent = new Intent(context, AlarmCreateActivity.class);
        intent.putExtra("alarm_id", -1);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MainMenuListManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
