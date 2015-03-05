package com.jitsik.memologue;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This manages the user's data.
 */
public class TaskStore extends BaseAdapter {

    private static final String FILENAME = "tasks.json";

    private class StoredTask {
        public Task task;
        public long identifier;

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

        public JSONObject toJson() {
            Map<String, Object> m = new HashMap<>();
            m.put("name", task.getName());
            m.put("last_done", new Long(task.getLastDone().getTime()));
            m.put("notify_period", new Long(task.getNotifyPeriod()));
            m.put("repeating", new Boolean(task.getRepeating()));
            m.put("times_done", new Integer(task.getTimesDone()));
            m.put("id", new Long(identifier));
            return new JSONObject(m);
        }
    }

    private static TaskStore singleton = null;
    private long currentId = 0;
    private ArrayList<StoredTask> tasks = new ArrayList();
    private LayoutInflater inflater;
    private Context context;

    private TaskStore(Context c) {
        context = c;
        parseFile(readFile());
    }

    public static TaskStore getTaskStore(Context c) {
        if (singleton != null) {
            return singleton;
        }
        singleton = new TaskStore(c);
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
                LogStore.getLogStore(context).addEntry(t.getName());
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

    public void save() {
        JSONObject[] tasksList = new JSONObject[tasks.size()];
        for (int i = 0; i < tasksList.length; ++i) {
            tasksList[i] = tasks.get(i).toJson();
        }
        Map m = new HashMap();
        m.put("tasks", tasksList);
        m.put("current_id", new Long(currentId));
        JSONObject full = new JSONObject(m);

        // Encode and save the data
        String encoded = full.toString();
        try {
            FileOutputStream outputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            outputStream.write(encoded.getBytes());
            outputStream.close();
        } catch (Exception e) {
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

    private String readFile() {
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
            return res.toString();
        } catch (Exception e) {
            // This is the default state.
            return "{\"current_id\": 0, \"tasks\": []}";
        }
    }

    private void parseFile(String file) {
        try {
            JSONObject obj = new JSONObject(file);
            currentId = obj.getInt("current_id");
            JSONArray arr = obj.getJSONArray("tasks");
            for (int i = 0; i < arr.length(); ++i) {
                JSONObject elem = arr.getJSONObject(i);
                tasks.add(new StoredTask(elem));
            }
        } catch (Exception e) {
            // Reset to the default state.
            currentId = 0;
            tasks.clear();
        }
    }
}
