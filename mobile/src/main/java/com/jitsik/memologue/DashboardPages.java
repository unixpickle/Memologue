package com.jitsik.memologue;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.Locale;

/**
 * This returns the two pages for the dashboard.
 */
public class DashboardPages extends FragmentPagerAdapter {

    public DashboardPages(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TasksFragment();
        } else {
            return new LogFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        if (i == 0) {
            return "TASKS";
        } else {
            return "LOG";
        }
    }
}
