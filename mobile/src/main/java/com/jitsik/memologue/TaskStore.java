package com.jitsik.memologue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * This manages the user's data.
 */
public class TaskStore extends BaseAdapter {

    private class StoredTask {
        public Task task;
        public long identifier;

        public StoredTask(Task t) {
            this.task = t;
            this.identifier = currentId++;
        }
    }

    private static TaskStore singleton = new TaskStore();
    private long currentId = 0;
    private ArrayList<StoredTask> tasks = new ArrayList<StoredTask>();
    private LayoutInflater inflater;

    private TaskStore() {
    }

    public static TaskStore getTaskStore() {
        return singleton;
    }

    public long add(Task t) {
        StoredTask st = new StoredTask(t);
        tasks.add(0, st);
        notifyDataSetChanged();
        return st.identifier;
    }

    public void didTask(long id) {
        for (int i = 0; i < tasks.size(); ++i) {
            if (tasks.get(i).identifier == id) {
                Task t = tasks.get(i).task;
                if (t.getRepeating()) {
                    StoredTask s = new StoredTask(tasks.get(i).task.taskByDoing());
                    tasks.set(i, s);
                } else {
                    tasks.remove(i);
                }
                LogStore.getLogStore().addEntry(t.getName());
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void remove(long id) {
        for (int i = 0; i < tasks.size(); ++i) {
            if (tasks.get(i).identifier == id) {
                tasks.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
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
        return tasks.get(position).identifier;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout rl = (RelativeLayout)convertView;
        if (rl == null) {
            rl = (RelativeLayout)getInflater().inflate(R.layout.task_cell, parent, false);
        }

        TextView nameView = (TextView)rl.findViewById(R.id.task_name);
        Task task = tasks.get(position).task;
        nameView.setText(task.getName());

        ElapsedLabel elapsed = (ElapsedLabel)rl.findViewById(R.id.elapsed_time);
        if (task.getTimesDone() == 0) {
            elapsed.setDate(null);
        } else {
            elapsed.setDate(task.getLastDone());
        }
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
