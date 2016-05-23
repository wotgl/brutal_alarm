package nazvaniye.nado.brutalalarm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import nazvaniye.nado.brutalalarm.activities.AlarmCreateActivity;

/**
 * Created by User on 23.05.2016.
 */
public class Adapter extends ArrayAdapter {
    Adapter THIS = this;

    public Adapter(Context context) {
        super(context, android.R.layout.simple_spinner_item, getList(context));
    }

    private static class Elem {
        public Elem(boolean activated, String time, String period, long id) {
            this.activated = activated;
            this.time = time;
            this.period = period;
            this.id = id;
        }

        boolean activated;
        String time;
        String period;
        long id;
    }

    private static ArrayList<Elem> getList(Context context) {
        ArrayList<Elem> arr = new ArrayList<>();

        Iterator<Data.AlarmTable> alarms = Data.AlarmTable.findAll(Data.AlarmTable.class);
        while(alarms.hasNext()) {
            Data.AlarmTable alarm = alarms.next();
            arr.add(new Elem(alarm.enabled, String.format("%02d:%02d", alarm.hour, alarm.minute), alarm.getDaysOfWeek().size() + " " + context.getString(R.string.times), alarm.getId()));
        }

        return arr;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.alarm_list_element, null);
        }

        final Elem el = (Elem) getItem(position);

        final CheckBox enabledCheckBox = (CheckBox) v.findViewById(R.id.enabledCheckBox);
        final TextView timeTextView = (TextView) v.findViewById(R.id.timeTextView);
        final Button removeButton = (Button) v.findViewById(R.id.removeButton);

        enabledCheckBox.setChecked(el.activated);
        timeTextView.setText(el.time + " | " + el.period);

        enabledCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.AlarmTable row = Data.AlarmTable.findById(Data.AlarmTable.class, el.id);
                row.enabled = enabledCheckBox.isChecked();
                row.save();
                Alarm.cheerUp(getContext());
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AlarmCreateActivity.class);
                intent.putExtra("alarm_id", (int)el.id);
                getContext().startActivity(intent);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.AlarmTable.findById(Data.AlarmTable.class, el.id).delete();
                THIS.remove(el);
                notifyDataSetChanged();
                Alarm.cheerUp(getContext());
            }
        });

        return v;
    }
}
