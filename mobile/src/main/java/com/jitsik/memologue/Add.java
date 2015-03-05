package com.jitsik.memologue;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Date;


public class Add extends ActionBarActivity {

    private EditText name;
    private CheckBox periodic;
    private CheckBox important;
    private View periodView;
    private NumberPicker[] periodPickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = (EditText)findViewById(R.id.name);
        periodic = (CheckBox)findViewById(R.id.periodic);
        important = (CheckBox)findViewById(R.id.important);
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

        periodic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton b, boolean c) {
                if (c) {
                    showPeriodicInfo();
                } else {
                    hidePeriodicInfo();
                }
            }
        });
    }

    private void hidePeriodicInfo() {
        important.setVisibility(View.GONE);
        periodView.setVisibility(View.GONE);
    }

    private void showPeriodicInfo() {
        important.setVisibility(View.VISIBLE);
        periodView.setVisibility(View.VISIBLE);
    }

    public void addTask(View v) {
        // Generate a Task and add it to the DataStore.
        Task t = DataStore.getDataStore().createTask();
        t.setName(name.getText().toString());
        t.setLastDone(new Date());
        if (periodic.isChecked()) {
            long period = (long)periodPickers[2].getValue() * 60 +
                    (long)periodPickers[1].getValue() * 60 * 60 +
                    (long)periodPickers[2].getValue() * 60 * 60 * 24;
            t.setPeriod(period);
        } else {
            t.setPeriod(-1);
        }
        t.setNotify(periodic.isChecked() && important.isChecked());
        DataStore.getDataStore().addTask(t);
        finish();
    }

}
