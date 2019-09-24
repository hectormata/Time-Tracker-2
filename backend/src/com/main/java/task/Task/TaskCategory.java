package com.main.java.task.Task;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.Map.Entry;

import com.main.java.task.Synchronisation.Account;
import main.GUI.stage1.main.TaskTracker;

public class TaskCategory implements Serializable {

    public static String getNewCategoryId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static final long serialVersionUID = 1L;
    private String						categoryId;
    private Date						categoryUpdated;
    private String						categoryName;
    private HashMap<String, String>		categorySyncIds;

    public TaskCategory() {
        categoryId = getNewCategoryId();
        categoryUpdated = new Date();
        categoryName = "Unnamed";
        categorySyncIds = new HashMap<String, String>();
    }

    public TaskCategory(String name) {
        categoryId = getNewCategoryId();
        categoryUpdated = new Date();
        categoryName = name;
        categorySyncIds = new HashMap<String, String>();
    }

    public TaskCategory(String name, Date updated) {
        categoryId = getNewCategoryId();
        categoryUpdated = updated;
        categoryName = name;
        categorySyncIds = new HashMap<String, String>();
    }

    public TaskCategory(String name, Date updated, String syncAccount, String syncId) {
        categoryId = getNewCategoryId();
        categoryUpdated = updated;
        categoryName = name;
        categorySyncIds = new HashMap<String, String>();
        categorySyncIds.put(syncAccount, syncId);
    }

    public String getId() {
        return categoryId;
    }

    public Date getUpdated() {
        return categoryUpdated;
    }

    public String getName() {
        return categoryName;
    }

    public String getSyncId(String syncAccount) {
        return categorySyncIds.get(syncAccount);
    }


    public HashMap<String, String> getSyncIds() {
        return categorySyncIds;
    }

    public boolean isCategoryIdentical(TaskCategory categoryRemote) {
        if (categoryId.equals(categoryRemote.getId())) {
            return true;
        }
        for (Iterator<Entry<String, String>> iterator = categoryRemote.getSyncIds().entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, String> entry = iterator.next();
            if (isCategoryIdentical(entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public Boolean isCategoryIdentical(String syncAccount, String syncIdent) {
        return !categorySyncIds.isEmpty() && categorySyncIds.containsKey(syncAccount) && categorySyncIds.get(syncAccount).equals(syncIdent);
    }

    public Boolean isCategoryWithinList(List<TaskCategory> list) {
        for (Iterator<TaskCategory> iterator = list.iterator(); iterator.hasNext(); ) {
            if (this.isCategoryIdentical( iterator.next() )) {
                return true;
            }
        }
        return false;
    }

    public void setUpdated(Date updated) {
        categoryUpdated = updated;
    }

    public void setName(String name) {
        categoryName = name;
    }

    public void setSyncIds(HashMap<String, String> syncIds) {
        categorySyncIds = syncIds;
    }

    public void sync() {
        Vector<Account> accounts = TaskTracker.getInstance().getAccounts();
        for (Iterator<Account> iterator = accounts.iterator(); iterator.hasNext(); ) {
            Account	account = iterator.next();
            if (categorySyncIds.containsKey(account.getAccountIdent())) {
                account.updateCategory(this);
            }
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}