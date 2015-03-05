package com.jitsik.memologue;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class Dashboard extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        DataStore.getDataStore().setInflater(getLayoutInflater());

        ListView v = (ListView)findViewById(R.id.dashboard_list);
        v.setAdapter(DataStore.getDataStore());
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
            this.startActivity(new Intent(this, Add.class));
            break;
        case R.id.action_log:
            this.startActivity(new Intent(this, Backlog.class));
            break;
        case R.id.action_settings:
            this.startActivity(new Intent(this, Settings.class));
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
