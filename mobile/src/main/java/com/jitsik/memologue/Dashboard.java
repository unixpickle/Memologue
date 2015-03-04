package com.jitsik.memologue;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

public class Dashboard extends FragmentActivity {

    DashboardPages adapter;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        adapter = new DashboardPages(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabs.setViewPager(pager);
    }

}
