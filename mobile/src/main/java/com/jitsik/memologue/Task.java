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
    private int timesDone = 0;

    public Task(String name, long period, Date lastDone, boolean notify, int timesDone) {
        this.name = name;
        this.period = period;
        this.lastDone = lastDone;
        this.notify = notify;
        this.timesDone = timesDone;
    }

    public Date getLastDone() {
        return lastDone;
    }

    public String getName() {
        return name;
    }

    public long getPeriod() {
        return period;
    }

    public boolean getNotify() {
        return notify;
    }

    public int getTimesDone() {
        return timesDone;
    }

    public Task taskByDoing() {
        return new Task(name, period, new Date(), notify, timesDone + 1);
    }

}
