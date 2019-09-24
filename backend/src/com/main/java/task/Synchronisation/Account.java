package com.main.java.task.Synchronisation;

import com.main.java.task.Task.Task;
import com.main.java.task.Task.TaskCategory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JPanel;

public abstract class Account {

    public static String getNewAccountIdent() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    protected String	accountIdent;

    public Account() {
        accountIdent = Account.getNewAccountIdent();
    }

    public Account(String ident) {
        accountIdent = ident;
    }

    public abstract void					createCategory(TaskCategory category);
    public abstract void					createTask(Task task);
    public abstract void					deleteCategory(TaskCategory category);
    public abstract void					deleteTask(Task task);
    public abstract String 					getName();
    public abstract String 					getType();
    public abstract JPanel					getConfiguration();
    public abstract Vector<TaskCategory>	getCategories(Vector<TaskCategory> categoryList);
    public abstract Vector<Task>			getTasks(Date updatedMin);
    public abstract void					updateCategory(TaskCategory category);
    public abstract void					updateTask(Task task, Task originalData);
    public abstract void					load(InputStream stream);
    public abstract void					save(OutputStream stream);

    public String getAccountIdent() {
        return accountIdent;
    }

    public Vector<TaskCategory> getCategories() {
        return this.getCategories(new Vector<TaskCategory>());
    }

    public Vector<Task> getTasks() {
        return this.getTasks(null);
    }

    @Override
    public String toString() {
        return this.getName();
    }

}