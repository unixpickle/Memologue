package com.jitsik.memologue;

import java.util.Date;

/**
 * This represents a task that the user can do.
 */
public class Task {

    private String name;
    private Date lastDone;
    private long notifyPeriod;
    private boolean repeating;
    private int timesDone = 0;

    public Task(String name, long notifyPeriod, Date lastDone, boolean repeating, int timesDone) {
        this.name = name;
        this.notifyPeriod = notifyPeriod;
        this.lastDone = lastDone;
        this.repeating = repeating;
        this.timesDone = timesDone;
    }

    public String getName() {
        return name;
    }

    public Date getLastDone() {
        return lastDone;
    }

    public long getNotifyPeriod() {
        return notifyPeriod;
    }

    public boolean getRepeating() {
        return repeating;
    }

    public int getTimesDone() {
        return timesDone;
    }

    public Task taskByDoing() {
        return new Task(name, notifyPeriod, new Date(), repeating, timesDone + 1);
    }

}
