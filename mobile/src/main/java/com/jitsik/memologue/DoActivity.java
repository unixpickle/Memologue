package com.jitsik.memologue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;


public class DoActivity extends ActionBarActivity {

    public static final String TASK_ID = "TASK_ID";
    private DatePicker datePicker;
    private TimePicker timePicker;
    private long taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do);
        Bundle extras = getIntent().getExtras();
        taskId = extras.getLong(TASK_ID);

        datePicker = (DatePicker)findViewById(R.id.date);
        timePicker = (TimePicker)findViewById(R.id.time);
    }

    public void donePressed(View v) {
        Calendar c = Calendar.getInstance();
        c.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(), 0);
        if (c.getTime().after(new Date())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.bad_date);
            builder.setPositiveButton(R.string.ok, null);
            builder.show();
            return;
        }
        Store store = Store.getStore();
        store.didTask(taskId, c.getTime());
        store.save();
        finish();
    }

}
