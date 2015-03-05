package com.jitsik.memologue;

import java.util.Date;

/**
 * This represents a task that the user can do.
 */
public class Task {

    private String name;
    private long period;
    private Date lastDone;
    private boolean notify;
    private long identifier;
    private int timesDone = 0;

    public Task(long id) {
        identifier = id;
    }

    public Date getLastDone() {
        return lastDone;
    }

    public void setLastDone(Date lastDone) {
        this.lastDone = lastDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public boolean getNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public long getIdentifier() {
        return identifier;
    }

    public int getTimesDone() {
        return timesDone;
    }

    public void setTimesDone(int timesDone) {
        this.timesDone = timesDone;
    }
}
