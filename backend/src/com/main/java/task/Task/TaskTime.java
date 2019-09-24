package com.main.java.task.Task;

import java.io.Serializable;
import java.util.Date;

public class TaskTime implements Serializable {

    public static String durationToString(Long duration) {

        Long s = (duration / 1000);
        return String.format("%d%s %d%s %d%s", s / 3600, "h", (s % 3600) / 60, "m", (s % 60), "s");
    }

    private static final long serialVersionUID = 3L;

    public enum Status {
        TRACKING, PAUSED, STOPPED
    };

    private Status	status;
    private Date	dateStart;
    private Date	dateEnd;
    private Long	timeIdle;

    public TaskTime() {
        status = Status.TRACKING;
        dateStart = new Date();
        dateEnd = new Date();
        timeIdle = 0L;
    }

    public void addIdleTime(Long idleDuration) {
        timeIdle += idleDuration;
    }

    public Status getStatus() {
        return status;
    }

    public Date getStart() {
        return dateStart;
    }

    public Date getEnd() {
        return dateEnd;
    }

    public Long getIdleTime() {
        return timeIdle;
    }

    public Long getWorkTime() {
        Long timeWork = dateEnd.getTime() - dateStart.getTime() - timeIdle;
        return (timeWork < 0 ? 0 : timeWork);
    }

    public void setStatus(Status newStatus) {
        status = newStatus;
    }

    public void setStart(Date start) {
        dateStart = start;
    }

    public void setEnd(Date end) {
        dateEnd = end;
    }

    public void setIdleTime(Long idleDuration) {
        timeIdle = idleDuration;
    }

}
