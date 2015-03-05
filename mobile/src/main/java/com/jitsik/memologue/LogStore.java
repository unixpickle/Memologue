package com.jitsik.memologue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This stores a backlog of tasks that were performed.
 */
public class LogStore extends BaseAdapter {

    private class Entry {

        public String taskName;
        public Date date;

        Entry(String n, Date d) {
            taskName = n;
            date = d;
        }

    }

    private static LogStore singleton = new LogStore();
    private ArrayList<Entry> entries = new ArrayList<Entry>();
    private LayoutInflater inflater;

    public static LogStore getLogStore() {
        return singleton;
    }

    private LogStore() {
    }

    public void addEntry(String name) {
        entries.add(new Entry(name, new Date()));
        notifyDataSetChanged();
    }

    public void clear() {
        entries.clear();
        notifyDataSetChanged();
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(getCount() - (position + 1));
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout rl = (RelativeLayout)convertView;
        if (rl == null) {
            rl = (RelativeLayout)getInflater().inflate(R.layout.log_cell, parent, false);
        }

        Entry e = (Entry)getItem(position);

        TextView nameView = (TextView)rl.findViewById(R.id.task_name);
        nameView.setText(e.taskName);

        TextView dateView = (TextView)rl.findViewById(R.id.task_date);
        DateFormat f = DateFormat.getDateTimeInstance();
        dateView.setText(f.format(e.date));

        return rl;
    }

}
