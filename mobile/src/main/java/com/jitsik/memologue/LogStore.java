package com.jitsik.memologue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * This stores a backlog of tasks that were performed.
 */
public class LogStore extends BaseAdapter {

    private static final String FILENAME = "log.json";

    private class Entry {

        public String taskName;
        public Date date;

        public Entry(String n, Date d) {
            taskName = n;
            date = d;
        }

        public Entry(JSONObject o) {
            taskName = o.optString("name");
            date = new Date(o.optLong("date"));
        }

        public JSONObject toJson() {
            Map<String, Object> map = new HashMap();
            map.put("name", taskName);
            map.put("date", date.getTime());
            return new JSONObject(map);
        }

    }

    private static LogStore singleton = null;
    private ArrayList<Entry> entries = new ArrayList<Entry>();
    private LayoutInflater inflater;
    private Context context;

    public static LogStore getLogStore(Context c) {
        if (singleton != null) {
            return singleton;
        }
        singleton = new LogStore(c);
        return singleton;
    }

    private LogStore(Context c) {
        context = c;
        parseFile(readFile());
    }

    public void addEntry(String name) {
        entries.add(new Entry(name, new Date()));
        notifyDataSetChanged();
    }

    public void clear() {
        entries.clear();
        notifyDataSetChanged();
    }

    public void save() {
        JSONObject[] entryList = new JSONObject[entries.size()];
        for (int i = 0; i < entryList.length; ++i) {
            entryList[i] = entries.get(i).toJson();
        }
        Map m = new HashMap();
        m.put("log_entries", entryList);
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
            return "{\"log_entries\": []}";
        }
    }

    private void parseFile(String file) {
        try {
            JSONObject obj = new JSONObject(file);
            JSONArray arr = obj.getJSONArray("log_entries");
            for (int i = 0; i < arr.length(); ++i) {
                JSONObject elem = arr.getJSONObject(i);
                entries.add(new Entry(elem));
            }
        } catch (Exception e) {
            // Reset to the default state.
            entries.clear();
        }
    }

}
