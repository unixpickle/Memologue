package com.jitsik.memologue;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This label displays the amount of time since a certain date, updating automatically.
 */
public class ElapsedLabel extends TextView {

    private Date date = null;
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture future = null;
    private boolean visible = false;
    private Handler updateHandler = new Handler();
    private Runnable updateRunnable;

    public ElapsedLabel(Context context) {
        super(context);
        init();
    }

    public ElapsedLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ElapsedLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date d) {
        this.date = d;

        // Restart the timer if it's running.
        if (visible) {
            stopTimer();
            startTimer();
        }
    }

    private void init() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                update();
            }
        };
        update();
    }

    private void startTimer() {
        if (future != null) {
            return;
        }

        update();

        if (date == null) {
            // Do not schedule anything if there is no date to update.
            return;
        }

        // Make sure the timer ticks a second after the minute count changes.
        long difference = (new Date().getTime() - date.getTime()) / 1000;
        long delay = 61 - (difference % 60);
        future = exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updateHandler.post(updateRunnable);
            }
        }, delay, 60, TimeUnit.SECONDS);
    }

    private void stopTimer() {
        if (future == null) {
            return;
        }
        future.cancel(false);
        future = null;
    }

    private void update() {
        if (date == null) {
            setText("Never done before");
            return;
        }

        // Possible options are: "A moment ago", "X minute(s) ago", "X hour(s) ago", "X day(s) ago",
        // "X day(s) and Y hour(s) ago".

        long difference = (new Date().getTime() - date.getTime()) / 1000;
        if (difference < 60) {
            setText("A moment ago");
            return;
        } else if (difference < 60 * 60) {
            long minutes = difference / 60;
            setText(minutes + " minute" + (minutes == 1 ? "" : "s") + " ago");
        } else if (difference < 60 * 60 * 24) {
            long hours = difference / (60 * 60);
            setText(hours + " hour" + (hours == 1 ? "" : "s") + " ago");
        } else {
            long hours = (difference / (60 * 60)) % 24;
            long days = difference / (60 * 60 * 24);
            String dayText = days + " day" + (days == 1 ? "" : "s");
            if (hours == 0) {
                setText(dayText + " ago");
            } else {
                String hourText = hours + " hour" + (hours == 1 ? "" : "s");
                setText(dayText + " and " + hourText + " ago");
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        visible = true;
        startTimer();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        visible = false;
        stopTimer();
    }

    @Override
    protected void onWindowVisibilityChanged(int state) {
        super.onWindowVisibilityChanged(state);
        if (state == VISIBLE) {
            visible = true;
            startTimer();
        } else {
            visible = false;
            stopTimer();
        }
    }

}
