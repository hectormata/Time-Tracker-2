package com.main.java.task.Task;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class TableTimeModel extends AbstractTableModel {

    static final Comparator<TaskTime> TASK_TIMES_ORDER_END_DESC = new Comparator<TaskTime>() {
        public int compare(TaskTime t1, TaskTime t2) {
            return t2.getEnd().compareTo(t1.getEnd());
        }
    };

    private static final long serialVersionUID = 1L;

    private Vector<TaskTime>	taskTimeList;

    public TableTimeModel() {
        super();
        taskTimeList = new Vector<TaskTime>();
    }

    public Vector<TaskTime> getTimes() {
        return taskTimeList;
    }

    public void setTimes(Vector<TaskTime> newTimeList) {
        Vector<TaskTime> listOrdered = new Vector<TaskTime>(newTimeList);
        Collections.sort(listOrdered, TASK_TIMES_ORDER_END_DESC);
        taskTimeList = listOrdered;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public int getRowCount() {
        return taskTimeList.size();
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Start time";
            case 1:
                return "End time";
            case 2:
                return "Idle duration";
            case 3:
                return "Work duration";
        }
        return super.getColumnName(col);
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row < taskTimeList.size()) {
            switch (col) {
                case 0:
                    return taskTimeList.get(row).getStart();
                case 1:
                    return taskTimeList.get(row).getEnd();
                case 2:
                    return TaskTime.durationToString( taskTimeList.get(row).getIdleTime() );
                case 3:
                    return TaskTime.durationToString( taskTimeList.get(row).getWorkTime() );
            }
        }
        return null;
    }

}