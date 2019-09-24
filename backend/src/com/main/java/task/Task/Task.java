package com.main.java.task.Task;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import com.main.java.task.Synchronisation.Account;
import main.GUI.stage1.main.TaskTracker;

public class Task implements Serializable {

    private static final long serialVersionUID = 3L;
    private	Boolean					taskDone;
    private	Boolean					taskHidden;
    private	Boolean					taskDeleted;
    private String					taskName;
    private String					taskCategory;
    private String					taskDescription;
    private Date					taskCreated;
    private Date					taskUpdated;
    private Date					taskCompleted;
    private Date					taskDue;
    private Vector<TaskTime>		taskTimes;
    private HashMap<String, String>	taskSyncIds;

    public Task() {
        taskDone = false;
        taskHidden = false;
        taskDeleted = false;
        taskName = "";
        taskCategory = "";
        taskDescription = "";
        taskCreated = new Date();
        taskUpdated = new Date();
        taskDue = null;
        taskCompleted = null;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = new HashMap<String, String>();
    }

    public Task(Task source) {
        taskDone = source.taskDone;
        taskHidden = source.taskHidden;
        taskDeleted = source.taskDeleted;
        taskName = source.taskName;
        taskCategory = source.taskCategory;
        taskDescription = source.taskDescription;
        taskCreated = source.taskCreated;
        taskUpdated = source.taskUpdated;
        taskDue = source.taskDue;
        taskCompleted = source.taskCompleted;
        taskTimes = source.taskTimes;
        taskSyncIds = source.taskSyncIds;
    }

    public Task(Boolean done, String name) {
        taskDone = done;
        taskHidden = false;
        taskDeleted = false;
        taskName = name;
        taskCategory = "";
        taskDescription = "";
        taskCreated = new Date();
        taskUpdated = new Date();
        taskDue = null;
        taskCompleted = null;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = new HashMap<String, String>();
    }

    public Task(Boolean done, String name, String category, String description, Date created, Date updated) {
        taskDone = done;
        taskHidden = false;
        taskDeleted = false;
        taskName = name;
        taskCategory = category;
        taskDescription = description;
        taskCreated = created;
        taskUpdated = updated;
        taskDue = null;
        taskCompleted = null;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = new HashMap<String, String>();
    }

    public Task(Boolean done, String name, String category, String description, Date created, Date updated, Date due) {
        taskDone = done;
        taskHidden = false;
        taskDeleted = false;
        taskName = name;
        taskCategory = category;
        taskDescription = description;
        taskCreated = created;
        taskUpdated = updated;
        taskDue = due;
        taskCompleted = null;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = new HashMap<String, String>();
    }

    public Task(Boolean done, String name, String category, String description, Date created, Date updated, Date due, HashMap<String, String> syncIds) {
        taskDone = done;
        taskHidden = false;
        taskDeleted = false;
        taskName = name;
        taskCategory = category;
        taskDescription = description;
        taskCreated = created;
        taskUpdated = updated;
        taskDue = due;
        taskCompleted = null;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = syncIds;
    }

    public Task(Boolean done, String name, String category, String description,
                Date created, Date updated, Date due, String syncAccount, String syncIdent) {
        taskDone = done;
        taskHidden = false;
        taskDeleted = false;
        taskName = name;
        taskCategory = category;
        taskDescription = description;
        taskCreated = created;
        taskUpdated = updated;
        taskDue = due;
        taskCompleted = null;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = new HashMap<String, String>();
        taskSyncIds.put(syncAccount, syncIdent);
    }

    public Task(Boolean done, Boolean hidden, Boolean deleted, String name, String category, String description,
                Date created, Date updated, Date due, String syncAccount, String syncIdent) {
        taskDone = done;
        taskHidden = (hidden == null ? false : hidden);
        taskDeleted = (deleted == null ? false : deleted);
        taskName = name;
        taskCategory = category;
        taskDescription = description;
        taskCreated = created;
        taskUpdated = updated;
        taskDue = due;
        taskCompleted = null;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = new HashMap<String, String>();
        taskSyncIds.put(syncAccount, syncIdent);
    }

    public Task(Boolean done, Boolean hidden, Boolean deleted, String name, String category, String description,
                Date created, Date updated, Date due, Date completed, String syncAccount, String syncIdent) {
        taskDone = done;
        taskHidden = (hidden == null ? false : hidden);
        taskDeleted = (deleted == null ? false : deleted);
        taskName = name;
        taskCategory = category;
        taskDescription = description;
        taskCreated = created;
        taskUpdated = updated;
        taskDue = due;
        taskCompleted = completed;
        taskTimes = new Vector<TaskTime>();
        taskSyncIds = new HashMap<String, String>();
        taskSyncIds.put(syncAccount, syncIdent);
    }

    public Boolean getDone() {
        return taskDone;
    }

    public Boolean getHidden() {
        return taskHidden;
    }

    public Boolean getDeleted() {
        return taskDeleted;
    }

    public String getName() {
        return taskName;
    }

    public TaskCategory getCategory() {
        return TaskTracker.getInstance().getCategory(taskCategory);
    }

    public String getCategoryId() {
        return taskCategory;
    }

    public String getDescription() {
        return taskDescription;
    }

    public Date getCreated() {
        return taskCreated;
    }

    public Date getUpdated() {
        return taskUpdated;
    }

    public Date getDue() {
        return taskDue;
    }

    public Date getCompleted() {
        return taskCompleted;
    }

    public TaskTime getTimeActive(Boolean allowCreate) {
        for (Iterator<TaskTime> iterator = taskTimes.iterator(); iterator.hasNext(); ) {
            TaskTime timeCurrent = iterator.next();
            if (timeCurrent.getStatus() != TaskTime.Status.STOPPED) {
                return timeCurrent;
            }
        }
        if (allowCreate) {
            // No active block found; create new one.
            TaskTime timeNew = new TaskTime();
            taskTimes.add(timeNew);
            return timeNew;
        }
        return null;
    }

    public Long getTimeWorked() {
        Long result = 0L;
        for (Iterator<TaskTime> iterator = taskTimes.iterator(); iterator.hasNext(); ) {
            TaskTime timeCurrent = iterator.next();
            result += timeCurrent.getWorkTime();
        }
        return result;
    }

    public Vector<TaskTime> getTimes() {
        return taskTimes;
    }

    public String getSyncId(String syncAccount) {
        return taskSyncIds.get(syncAccount);
    }

    public HashMap<String, String> getSyncIds() {
        return taskSyncIds;
    }

    public boolean isTaskIdentical(Task taskRemote) {
        for (Iterator<Entry<String, String>> iterator = taskRemote.taskSyncIds.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, String> entry = iterator.next();
            if (isTaskIdentical(entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public Boolean isTaskIdentical(String syncAccount, String syncIdent) {
        return !taskSyncIds.isEmpty() && taskSyncIds.containsKey(syncAccount) && taskSyncIds.get(syncAccount).equals(syncIdent);
    }

    public void setDone(Boolean done) {
        taskDone = done;
    }

    public void setHidden(Boolean hidden) {
        taskHidden = hidden;
    }

    public void setDeleted(Boolean deleted) {
        taskDeleted = deleted;
    }

    public void setName(String name) {
        taskName = name;
    }

    public void setCategory(String category) {
        taskCategory = category;
    }

    public void setCategory(TaskCategory category) {
        taskCategory = category.getId();
        // Add to category list if unknown
        Vector<TaskCategory> categories = TaskTracker.getInstance().getCategories();
        if (!categories.contains(category)) {
            categories.add(category);
            TaskTracker.getInstance().updateCategories();
        }
    }

    public void setDescription(String description) {
        taskDescription = description;
    }

    public void setCreated(Date created) {
        taskCreated = created;
    }

    public void setUpdated(Date updated) {
        taskUpdated = updated;
    }

    public void setCompleted(Date completed) {
        taskCompleted = completed;
    }

    public void setDue(Date due) {
        taskDue = due;
    }

    public void setSyncIds(HashMap<String, String> syncIds) {
        taskSyncIds = syncIds;
    }

    public void setTimes(Vector<TaskTime> times) {
        taskTimes = times;
    }

    public void startTracking() {
        // No active block found; create new one.
        TaskTime time = getTimeActive(true);
        if (time.getStatus() == TaskTime.Status.PAUSED) {
            // Resume!
            Date now = new Date();
            Long idleTime = now.getTime() - time.getEnd().getTime();
            time.addIdleTime(idleTime);
            time.setEnd(now);
            time.setStatus(TaskTime.Status.TRACKING);
        } else if (time.getStatus() == TaskTime.Status.TRACKING) {
            // Already running, just update stamp
            time.setEnd(new Date());
            time.setStatus(TaskTime.Status.TRACKING);
        }
    }

    public void pauseTracking() {
        TaskTime time = getTimeActive(false);
        if (time != null) {
            time.setEnd(new Date());
            time.setStatus(TaskTime.Status.PAUSED);
        }
    }

    public void stopTracking() {
        TaskTime time = getTimeActive(false);
        if (time != null) {
            Date now = new Date();
            if (time.getStatus() == TaskTime.Status.PAUSED) {
                // Add idle time between pause and now
                time.addIdleTime( now.getTime() - time.getEnd().getTime() );
            }
            time.setEnd(now);
            time.setStatus(TaskTime.Status.STOPPED);
        }
    }

    public void sync(Task originalData) {
        Vector<Account>	accounts = TaskTracker.getInstance().getAccounts();
        for (Iterator<Account> iterator = accounts.iterator(); iterator.hasNext(); ) {
            Account	account = iterator.next();
            if (taskSyncIds.containsKey(account.getAccountIdent())) {
                account.updateTask(this, originalData);
            } else {
                account.createTask(this);
            }
        }
    }
}
