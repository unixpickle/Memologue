package com.jitsik.memologue;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class BacklogActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlog);

        LogStore store = LogStore.getLogStore(getApplicationContext());
        store.setInflater(getLayoutInflater());

        ListView v = (ListView)findViewById(R.id.log_list);
        v.setAdapter(store);
    }

}
