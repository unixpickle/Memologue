package com.jitsik.memologue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This manages the user's data.
 */
public class DataStore extends BaseAdapter {

    private static DataStore singleton = new DataStore();
    private long currentId = 0;
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private LayoutInflater inflater;

    private DataStore() {
    }

    public static DataStore getDataStore() {
        return singleton;
    }

    public Task createTask() {
        return new Task(currentId++);
    }

    public void addTask(Task t) {
        tasks.add(0, t);
        notifyDataSetChanged();
    }

    public void removeTask(Task t) {
        tasks.remove(t);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).getIdentifier();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout rl = (RelativeLayout)convertView;
        if (rl == null) {
            rl = (RelativeLayout)getInflater().inflate(R.layout.task_cell, parent, false);
        }
        TextView nameView = (TextView)rl.findViewById(R.id.task_name);
        nameView.setText(tasks.get(position).getName());
        return rl;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }
}
