package com.jitsik.memologue;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Date;


public class AddActivity extends ActionBarActivity {

    private EditText name;
    private CheckBox repeating;
    private CheckBox notify;
    private View periodView;
    private NumberPicker[] periodPickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = (EditText)findViewById(R.id.name);
        repeating = (CheckBox)findViewById(R.id.repeating);
        notify = (CheckBox)findViewById(R.id.notify);
        periodView = findViewById(R.id.period_view);

        periodPickers = new NumberPicker[3];
        periodPickers[0] = (NumberPicker)findViewById(R.id.days);
        periodPickers[1] = (NumberPicker)findViewById(R.id.hours);
        periodPickers[2] = (NumberPicker)findViewById(R.id.minutes);
        for (NumberPicker p : periodPickers) {
            p.setMinValue(0);
        }
        periodPickers[0].setMaxValue(365);
        periodPickers[1].setMaxValue(23);
        periodPickers[2].setMaxValue(59);
        periodPickers[0].setValue(1);

        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton b, boolean c) {
                if (c) {
                    showTimePicker();
                } else {
                    hideTimePicker();
                }
            }
        });
    }

    private void hideTimePicker() {
        periodView.setVisibility(View.GONE);
    }

    private void showTimePicker() {
        periodView.setVisibility(View.VISIBLE);
    }

    public void addTask(View v) {
        // Generate a Task and add it to the DataStore.

        String nameStr = name.getText().toString();
        long period = -1;
        Date lastDone = new Date();
        boolean rep = repeating.isChecked();

        if (notify.isChecked()) {
            period = (long)periodPickers[2].getValue() * 60 +
                    (long)periodPickers[1].getValue() * 60 * 60 +
                    (long)periodPickers[2].getValue() * 60 * 60 * 24;
        }

        TaskStore.getTaskStore().add(new Task(nameStr, period, lastDone, rep, 0));
        finish();
    }

}
