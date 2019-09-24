package com.main.java.task.Task;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class TableTaskModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private Vector<Task>	taskList;

    public TableTaskModel() {
        super();
        taskList = new Vector<Task>();
    }

    public Vector<Task> getTasks() {
        return taskList;
    }

    public void setTasks(Vector<Task> newTaskList) {
        taskList = newTaskList;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public int getRowCount() {
        return taskList.size();
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Done";
            case 1:
                return "Task";
            case 2:
                return "Work time";
            case 3:
                return "Created";
            case 4:
                return "Due";
        }
        return super.getColumnName(col);
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row < taskList.size()) {
            switch (col) {
                case 0:
                    return taskList.get(row).getDone();
                case 1:
                    return taskList.get(row).getName();
                case 2:
                    return TaskTime.durationToString( taskList.get(row).getTimeWorked() );
                case 3:
                    return taskList.get(row).getCreated();
                case 4:
                    return taskList.get(row).getDue();
            }
        }
        return null;
    }

}