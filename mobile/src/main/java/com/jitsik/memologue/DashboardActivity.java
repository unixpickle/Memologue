package com.jitsik.memologue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class DashboardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Store.setup(getApplicationContext());

        ListView v = (ListView)findViewById(R.id.dashboard_list);
        v.setAdapter(Store.getStore().getTasksAdapter());

        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int i, long id) {
                handleClicked(id);
            }
        });
        v.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p, View v, int i, long id) {
                handleDeletion(id);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add:
            this.startActivity(new Intent(this, AddActivity.class));
            break;
        case R.id.action_log:
            this.startActivity(new Intent(this, BacklogActivity.class));
            break;
        case R.id.action_settings:
            this.startActivity(new Intent(this, SettingsActivity.class));
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        Store.getStore().save();
    }

    public void handleClicked(long id) {
        Intent i = new Intent(this, DoActivity.class);
        i.putExtra(DoActivity.TASK_ID, id);
        this.startActivity(i);
    }

    public void handleDeletion(final long id) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Delete task");
        b.setMessage("Are you sure you want to delete this task?");
        b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Store.getStore().removeTask(id);
            }
        });
        b.setNegativeButton(android.R.string.no, null);
        b.show();
    }

}
