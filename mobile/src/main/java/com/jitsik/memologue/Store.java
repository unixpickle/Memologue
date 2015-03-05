package com.jitsik.memologue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This manages the data of the application.
 */
public class Store {

    private static final String FILENAME = "data.json";

    private Context context;
    private long currentId = 0;
    private ArrayList<StoredTask> tasks = new ArrayList<>();
    private ArrayList<LogEntry> log = new ArrayList<>();
    private LogAdapter logAdapter = new LogAdapter();
    private TasksAdapter tasksAdapter = new TasksAdapter();
    private static Store singleton = null;

    private Store(Context c) {
        context = c;
    }

    public static void setup(Context c) {
        if (singleton != null) {
            return;
        }
        singleton = new Store(c);
        singleton.load();
    }

    public static Store getStore() {
        if (singleton == null) {
            throw new NullPointerException("Store singleton is not initialized.");
        }
        return singleton;
    }

    public long addTask(Task t) {
        StoredTask st = new StoredTask(t);
        tasks.add(0, st);
        tasksAdapter.notifyDataSetChanged();
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
                log.add(new LogEntry(t.getName(), new Date()));
                tasksAdapter.notifyDataSetChanged();
                logAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void removeTask(long id) {
        for (int i = 0; i < tasks.size(); ++i) {
            if (tasks.get(i).identifier == id) {
                tasks.remove(i);
                tasksAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public ListAdapter getLogAdapter() {
        return logAdapter;
    }

    public ListAdapter getTasksAdapter() {
        return tasksAdapter;
    }

    public void save() {
        JSONObject[] tasksList = new JSONObject[tasks.size()];
        for (int i = 0; i < tasksList.length; ++i) {
            tasksList[i] = tasks.get(i).toJSON();
        }
        JSONObject[] logList = new JSONObject[log.size()];
        for (int i = 0; i < logList.length; ++i) {
            logList[i] = log.get(i).toJSON();
        }
        Map<String, Object> m = new HashMap<>();
        m.put("current_id", currentId);
        m.put("log", logList);
        m.put("tasks", tasksList);
        JSONObject full = new JSONObject(m);

        // Encode and save the data
        String encoded = full.toString();
        try {
            FileOutputStream outputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            outputStream.write(encoded.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load() {
        // Attempt to load the JSON string from a file.
        String jsonString;
        try {
            FileInputStream stream = context.openFileInput(FILENAME);
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader b = new BufferedReader(reader);
            StringBuilder res = new StringBuilder();
            while (true) {
                String s = b.readLine();
                if (s == null) {
                    break;
                }
                res.append(s);
            }
            stream.close();
            jsonString = res.toString();
        } catch (Exception e) {
            return;
        }

        // Attempt to process the JSON.
        try {
            JSONObject obj = new JSONObject(jsonString);
            currentId = obj.getInt("current_id");

            // Load tasks.
            JSONArray arr = obj.getJSONArray("tasks");
            for (int i = 0; i < arr.length(); ++i) {
                JSONObject elem = arr.getJSONObject(i);
                tasks.add(new StoredTask(elem));
            }

            // Load log.
            arr = obj.getJSONArray("log");
            for (int i = 0; i < arr.length(); ++i) {
                JSONObject elem = arr.getJSONObject(i);
                log.add(new LogEntry(elem));
            }
        } catch (Exception e) {
            currentId = 0;
            tasks.clear();
            log.clear();
        }
    }

    private class StoredTask {
        public long identifier;
        public Task task;

        public StoredTask(Task t) {
            this.task = t;
            this.identifier = currentId++;
        }

        public StoredTask(JSONObject o) {
            Log.v("DashboardActivity", "object is " + o);
            String name = o.optString("name");
            long period = o.optLong("notify_period");
            Date lastDone = new Date(o.optLong("last_done"));
            boolean repeating = o.optBoolean("repeating");
            int times = o.optInt("times_done");
            identifier = o.optLong("identifier", currentId++);
            task = new Task(name, period, lastDone, repeating, times);
        }

        public JSONObject toJSON() {
            Map<String, Object> m = new HashMap<>();
            m.put("name", task.getName());
            m.put("last_done", task.getLastDone().getTime());
            m.put("notify_period", task.getNotifyPeriod());
            m.put("repeating", task.getRepeating());
            m.put("times_done", task.getTimesDone());
            m.put("id", identifier);
            return new JSONObject(m);
        }
    }

    private class LogEntry {
        public String name;
        public Date date;

        public LogEntry(String n, Date d) {
            name = n;
            date = d;
        }

        public LogEntry(JSONObject o) {
            name = o.optString("name");
            date = new Date(o.optLong("date"));
        }

        public JSONObject toJSON() {
            Map<String, Object> m = new HashMap<>();
            m.put("name", name);
            m.put("date", date.getTime());
            return new JSONObject(m);
        }
    }

    private class LogAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return log.size();
        }

        @Override
        public Object getItem(int position) {
            return log.get(getCount() - (position + 1));
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

            LogEntry e = (LogEntry)getItem(position);

            TextView nameView = (TextView)rl.findViewById(R.id.task_name);
            nameView.setText(e.name);

            TextView dateView = (TextView)rl.findViewById(R.id.task_date);
            DateFormat f = DateFormat.getDateTimeInstance();
            dateView.setText(f.format(e.date));

            return rl;
        }

        public LayoutInflater getInflater() {
            return LayoutInflater.from(context);
        }
    }

    private class TasksAdapter extends BaseAdapter {
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
            return LayoutInflater.from(context);
        }
    }

}
