package nazvaniye.nado.brutalalarm.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;

import java.util.ArrayList;

import nazvaniye.nado.brutalalarm.BaseActivity;
import nazvaniye.nado.brutalalarm.Data;
import nazvaniye.nado.brutalalarm.R;

public class AlarmCreateActivity extends BaseActivity {

    CharSequence[] difficultlyNames;

    private boolean creatingNewAlarm;
    private int alarmId;

    private boolean enabled = false;
    private int[] time = {0,0};
    private ArrayList<Integer> daysOfWeek = new ArrayList<>();
    private int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.alarm_create);
        super.onCreate(savedInstanceState);

        difficultlyNames = new CharSequence[]{
                getString(R.string.hard).toUpperCase(),
                getString(R.string.very_hard).toUpperCase(),
                getString(R.string.the_hardest).toUpperCase(),
                getString(R.string.impossible).toUpperCase()};

        Intent intent = getIntent();
        alarmId = intent.getIntExtra("alarm_id", -100);
        creatingNewAlarm = alarmId < 0;

        if(!creatingNewAlarm) {
            Data.AlarmTable row = Data.AlarmTable.findById(Data.AlarmTable.class, alarmId);
            enabled = row.enabled;
            time = new int[]{row.hour, row.minute};
            daysOfWeek = row.getDaysOfWeek();
            difficulty = row.difficulty;
        }

        refreshInfo();
    }

    private void refreshInfo() {
        ((CheckBox)findViewById(R.id.alarmEnabledCheckBox)).setChecked(enabled);
        ((TextView)findViewById(R.id.timeTextView)).setText(String.format("%02d:%02d", time[0], time[1]));
        ((TextView)findViewById(R.id.repeatTextView)).setText(
                daysOfWeek.size() == 7 ? getString(R.string.every_day) : daysOfWeek.size() == 0 ? getString(R.string.never) : daysOfWeek.size() + " " + getString(R.string.times)
        );
        ((TextView)findViewById(R.id.difficultyTextView)).setText(difficultlyNames[difficulty]);
    }

    public void enabledClick(View v) {
        enabled = ((CheckBox)v).isChecked();
        refreshInfo();
    }

    public void timeClick(View v) {
        TimePickerBuilder tpb = new TimePickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .addTimePickerDialogHandler(new TimePickerDialogFragment.TimePickerDialogHandler() {
                    @Override
                    public void onDialogTimeSet(int i, int i1, int i2) {
                        time = new int[]{i1, i2};
                        refreshInfo();
                    }
                })
                .setStyleResId(R.style.BetterPickersDialogFragment);
        tpb.show();
    }

    public void repeatClick(View v) {
        final String title = getString(R.string.day_of_week);
        final CharSequence[] items = {
                getString(R.string.monday),
                getString(R.string.tuesday),
                getString(R.string.wednesday),
                getString(R.string.thursday),
                getString(R.string.friday),
                getString(R.string.saturday),
                getString(R.string.sunday)};
        final ArrayList<Integer> daysOfWeekPrepare = daysOfWeek;

        final boolean[] selectedItems = {false, false, false, false, false, false, false};
        for(int i = 0; i < daysOfWeek.size(); ++i) {
            selectedItems[daysOfWeek.get(i)] = true;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(getString(R.string.ok).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        daysOfWeek = daysOfWeekPrepare;
                        refreshInfo();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setMultiChoiceItems(items, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            daysOfWeekPrepare.add(indexSelected);
                        } else if (daysOfWeekPrepare.contains(indexSelected)) {
                            daysOfWeekPrepare.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).create();
        dialog.show();
    }

    public void difficultyClick(View v) {
        final String title = getString(R.string.difficulty);
        final Integer[] difficultyPrepare = {difficulty};

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(getString(R.string.ok).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        difficulty = difficultyPrepare[0];
                        refreshInfo();
                    }
                }).setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setSingleChoiceItems(difficultlyNames, difficultyPrepare[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        difficultyPrepare[0] = which;
                    }
                })
                .create();
        dialog.show();
    }

    public void okClick(View v) {
        if(creatingNewAlarm) {
            Data.AlarmTable alarm = new Data.AlarmTable(enabled, time[0], time[1], daysOfWeek, difficulty);
            alarm.save();
            finish();
        } else {
            Data.AlarmTable alarm = Data.AlarmTable.findById(Data.AlarmTable.class, alarmId);
            alarm.enabled = enabled;
            alarm.hour = time[0];
            alarm.minute = time[1];
            alarm.setDaysOfWeek(daysOfWeek);
            alarm.difficulty = difficulty;
            alarm.save();
            finish();
        }
    }

    public void cancelClick(View v) {
        finish();
    }

}
