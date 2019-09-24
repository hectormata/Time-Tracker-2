package com.main.java.task.Windows;

import com.main.java.task.Task.TableTimeModel;
import com.main.java.task.Task.Task;
import com.main.java.task.Task.TaskCategory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.EventListener;
import java.util.Properties;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.main.java.task.Task.TaskTime;
import main.GUI.stage1.main.TaskTracker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;


public class TaskPanel extends JPanel implements ChangeListener, DocumentListener, ItemListener {

    public abstract interface TaskListSyncronisationListener extends EventListener {

        public abstract void clearSelection ();

        public abstract void discardedTask (Task task, Boolean previousWasEmpty);

        public abstract void selectTask (Task task);

        public abstract void pauseTracking (Task task);

        public abstract void savedTask (Task task);

        public abstract void startTracking (Task task);

        public abstract void stopTracking (Task task);

    }

    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;
    private Task taskCurrent;
    private Boolean taskCurrentDirty;
    private Boolean taskCurrentEmpty;
    private JPanel taskPanel;
    private JTextField taskPanelTitle;
    private JComboBox<TaskCategory> taskPanelCategory;
    private Vector<TaskCategory> taskPanelCategoryList;
    private JTextArea taskPanelDescription;
    private JDatePickerImpl taskPanelDueDate;
    private UtilDateModel taskPanelDueDateModel;
    private JDatePickerImpl taskPanelCompletionDate;
    private UtilDateModel taskPanelCompletionDateModel;
    private JButton taskPanelCompletionButton;
    private JButton buttonsTimeTrack;
    private JButton buttonsTimePause;
    private JButton buttonsTimeStop;
    private JButton buttonsNew;
    private JButton buttonsSave;
    private JPanel timePanel;
    private TableTimeModel timeTableModel;
    private JTable timeTable;
    private JScrollPane timeTableScroll;

    public TaskPanel () {
        super(new BorderLayout());
        /**
         * General tab
         */
        taskPanel = new JPanel(new BorderLayout());
        taskCurrent = new Task();
        taskCurrentDirty = false;
        taskCurrentEmpty = true;
        JPanel panelCenter = new JPanel();
        JPanel panelRight = new JPanel();
        // Setup input components
        JFormattedTextField.AbstractFormatter dateFormatter = new DatePickerFormatter();
        Properties dateStrings = new Properties();
        dateStrings.put("text.today", "Today");
        dateStrings.put("text.month", "Month");
        dateStrings.put("text.year", "Year");
        taskPanelDueDateModel = new UtilDateModel();
        taskPanelDueDateModel.addChangeListener(this);
        taskPanelCompletionDateModel = new UtilDateModel();
        taskPanelCompletionDateModel.addChangeListener(this);
        JDatePanelImpl datePanelDue = new JDatePanelImpl(taskPanelDueDateModel, dateStrings);
        JDatePanelImpl datePanelCompletion = new JDatePanelImpl(taskPanelCompletionDateModel, dateStrings);
        taskPanelTitle = new JTextField();
        taskPanelTitle.getDocument().addDocumentListener(this);
        taskPanelCategoryList = new Vector<TaskCategory>();
        taskPanelCategory = new JComboBox<TaskCategory>(taskPanelCategoryList);
        taskPanelCategory.addItemListener(this);
        taskPanelCategory.setEditable(true);
        taskPanelDescription = new JTextArea();
        taskPanelDescription.getDocument().addDocumentListener(this);
        taskPanelDescription.setPreferredSize(new Dimension(300, 100));
        taskPanelDueDate = new JDatePickerImpl(datePanelDue, dateFormatter);
        taskPanelCompletionDate = new JDatePickerImpl(datePanelCompletion, dateFormatter);
        taskPanelCompletionButton = new JButton("Set task completed");
        taskPanelCompletionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent event) {
                taskPanelCompletionDateModel.setValue(new Date());
            }
        });
        // Setup labels
        JLabel labelTitle = new JLabel("Title");
        JLabel labelCategory = new JLabel("Category");
        JLabel labelDescription = new JLabel("Description");
        JLabel labelDueDate = new JLabel("Due date");
        JLabel labelCompletionDate = new JLabel("Completion date");
        // Setup layout
        GroupLayout layoutCenter = new GroupLayout(panelCenter);
        GroupLayout layoutRight = new GroupLayout(panelRight);
        panelCenter.setLayout(layoutCenter);
        layoutCenter.setAutoCreateGaps(true);
        layoutCenter.setAutoCreateContainerGaps(true);
        layoutCenter.setHorizontalGroup(
                layoutCenter.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layoutCenter.createSequentialGroup()
                                        .addComponent(labelTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(taskPanelTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(
                                layoutCenter.createSequentialGroup()
                                        .addComponent(labelCategory, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(taskPanelCategory, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(
                                layoutCenter.createSequentialGroup()
                                        .addComponent(labelDescription, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(taskPanelDescription, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
        );
        layoutCenter.setVerticalGroup(
                layoutCenter.createSequentialGroup()
                        .addGroup(
                                layoutCenter.createParallelGroup()
                                        .addComponent(labelTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(taskPanelTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(
                                layoutCenter.createParallelGroup()
                                        .addComponent(labelCategory, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(taskPanelCategory, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(
                                layoutCenter.createParallelGroup()
                                        .addComponent(labelDescription, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(taskPanelDescription, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
        );
        layoutCenter.linkSize(SwingConstants.HORIZONTAL, labelTitle, labelCategory, labelDescription);
        layoutCenter.linkSize(SwingConstants.HORIZONTAL, taskPanelTitle, taskPanelCategory, taskPanelDescription);
        panelRight.setLayout(layoutRight);
        layoutRight.setHorizontalGroup(
                layoutRight.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(labelDueDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(taskPanelDueDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelCompletionDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(taskPanelCompletionDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(taskPanelCompletionButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        layoutRight.setVerticalGroup(
                layoutRight.createSequentialGroup()
                        .addComponent(labelDueDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(taskPanelDueDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelCompletionDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(taskPanelCompletionDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(taskPanelCompletionButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        layoutRight.linkSize(SwingConstants.HORIZONTAL, taskPanelCompletionDate, taskPanelCompletionButton);
        // Add to main panel
        taskPanel.add(panelCenter, BorderLayout.CENTER);
        taskPanel.add(panelRight, BorderLayout.EAST);
        /**
         * Time tracking tab
         */
        timePanel = new JPanel(new BorderLayout());
        timeTableModel = new TableTimeModel();
        timeTable = new JTable(timeTableModel);
        timeTable.setFillsViewportHeight(true);
        timeTableScroll = new JScrollPane(timeTable);
        timeTableScroll.setPreferredSize(new Dimension(640, 200));
        timePanel.add(timeTableScroll, BorderLayout.CENTER);
        /**
         * Button bar
         */
        JPanel panelButtons = new JPanel(new BorderLayout());
        JPanel panelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // Setup right aligned buttons
        // - New / discard
        buttonsNew = new JButton("Discard / New (DEL)");
        buttonsNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.newTask();
            }
        });
        buttonsNew.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "discard");
        buttonsNew.getActionMap().put("discard", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.newTask();
            }
        });
        // - Save
        buttonsSave = new JButton("Save task (F4)");
        buttonsSave.setEnabled(false);
        buttonsSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.saveTask();
            }
        });
        buttonsSave.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F4"), "save");
        buttonsSave.getActionMap().put("save", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.saveTask();
            }
        });
        // - Fill right panel
        panelButtonsRight.add(buttonsNew);
        panelButtonsRight.add(buttonsSave);
        // Setup left aligned buttons
        // - Start work
        buttonsTimeTrack = new JButton("Start work (F1)");
        buttonsTimeTrack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.timeTrackStart();
            }
        });
        buttonsTimeTrack.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F1"), "start");
        buttonsTimeTrack.getActionMap().put("start", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.timeTrackStart();
            }
        });
        // - Pause work
        buttonsTimePause = new JButton("Pause work (F2)");
        buttonsTimePause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.timeTrackPause();
            }
        });
        buttonsTimePause.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"), "pause");
        buttonsTimePause.getActionMap().put("pause", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.timeTrackPause();
            }
        });
        // - Stop work
        buttonsTimeStop = new JButton("Stop work (F3)");
        buttonsTimeStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.timeTrackStop();
            }
        });
        buttonsTimeStop.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F3"), "stop");
        buttonsTimeStop.getActionMap().put("stop", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed (ActionEvent arg0) {
                TaskPanel.this.timeTrackStop();
            }
        });
        // - Fill left panel
        panelButtonsLeft.add(buttonsTimeTrack);
        panelButtonsLeft.add(buttonsTimePause);
        panelButtonsLeft.add(buttonsTimeStop);
        // Add to button panel
        panelButtons.add(panelButtonsLeft, BorderLayout.WEST);
        panelButtons.add(panelButtonsRight, BorderLayout.EAST);
        /**
         * Tabbed pane / main panel
         */
        // Title
        JLabel labelHeading = new JLabel("Edit / create Task");
        labelHeading.setFont(new Font("Arial", Font.BOLD, 20));
        JPanel panelHeading = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelHeading.add(labelHeading);
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.add(taskPanel, "General");
        tabbedPane.add(timePanel, "Time tracking");
        // Add to main panel
        add(panelHeading, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
        // Update UI
        updateTaskTimes();
        // Remove dirty flag
        setDirty(false);
    }

    protected void timeTrackStart () {
        taskCurrent.startTracking();
        // Update time table
        updateTaskTimes();
        // Set dirty
        setDirty(true);
        // Save updated task to file
        TaskTracker.getInstance().saveTasks();
        // Call syncronisation listeners
        TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
        for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
            listeners[listenerIndex].startTracking(taskCurrent);
        }
    }

    protected void timeTrackStop () {
        taskCurrent.stopTracking();
        // Update time table
        updateTaskTimes();
        // Set dirty
        setDirty(true);
        // Save updated task to file
        TaskTracker.getInstance().saveTasks();
        // Call syncronisation listeners
        TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
        for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
            listeners[listenerIndex].stopTracking(taskCurrent);
        }
    }

    protected void timeTrackPause () {
        taskCurrent.pauseTracking();
        // Update time table
        updateTaskTimes();
        // Set dirty
        setDirty(true);
        // Save updated task to file
        TaskTracker.getInstance().saveTasks();
        // Call syncronisation listeners
        TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
        for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
            listeners[listenerIndex].pauseTracking(taskCurrent);
        }
    }

    public void addSyncronisationListener (TaskListSyncronisationListener listener) {
        this.listenerList.add(TaskListSyncronisationListener.class, listener);
    }

    public Task getTask () {
        return taskCurrent;
    }

    public boolean checkTaskSaved () {
        if (taskCurrentDirty) {
            Object[] optionsDirty = {"Cancel", "Discard and continue", "Save and continue"};
            int result = JOptionPane.showOptionDialog(
                    this, "You have unsaved changes to the current task! How do you want to proceed?", "Unsaved changes!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, optionsDirty, optionsDirty[0]
            );
            switch (result) {
                // Cancel
                case 0:
                    // Update selection
                    TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
                    for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
                        listeners[listenerIndex].selectTask(taskCurrent);
                    }
                    return false;
                // Discard
                case 1:
                    break;
                // Save
                default:
                case 2:
                    saveTask();
                    break;
            }
        }
        return true;
    }

    public Boolean newTask () {
        Task newTask = new Task();
        Boolean currentIsEmpty = taskCurrentEmpty;
        if (editTask(newTask)) {
            taskCurrentEmpty = true;
            // Call syncronisation listeners
            TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
            for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
                listeners[listenerIndex].discardedTask(taskCurrent, currentIsEmpty);
            }
            return true;
        }
        return false;
    }

    public Boolean editTask (Task selectedTask) {
        return editTask(selectedTask, false);
    }

    public Boolean editTask (Task selectedTask, Boolean ignoreDirty) {
        if (selectedTask.equals(taskCurrent)) {
            // Task did not change
            return true;
        } else if (taskCurrentDirty && ! ignoreDirty) {
            // Time tracking active?
            TaskTime timeCurrent = taskCurrent.getTimeActive(false);
            if ((timeCurrent != null) && (timeCurrent.getStatus() == TaskTime.Status.TRACKING)) {
                Object[] optionsTracking = {"Cancel", "Pause tracking", "Stop tracking", "Continue anyway"};
                int result = JOptionPane.showOptionDialog(
                        this, "You are currently tracking time for this task! How do you want to proceed?", "Time tracking active!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsTracking, optionsTracking[0]
                );
                switch (result) {
                    // Cancel
                    case 0:
                        // Update selection
                        TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
                        for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
                            listeners[listenerIndex].selectTask(taskCurrent);
                        }
                        return false;
                    // Pause
                    default:
                    case 1:
                        timeTrackPause();
                        break;
                    // Stop
                    case 2:
                        timeTrackStop();
                        break;
                    // Continue
                    case 3:
                        break;
                }
            }
            if (! checkTaskSaved()) {
                return false;
            }
        }
        taskCurrent = selectedTask;
        taskCurrentEmpty = false;
        // Update UI
        updateTaskInputs();
        updateTaskTimes();
        // Clear dirty flag
        setDirty(false);
        // Update selection
        TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
        for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
            listeners[listenerIndex].selectTask(taskCurrent);
        }
        return true;
    }

    public void updateTaskInputs () {
        // Update inputs
        taskPanelTitle.setText(taskCurrent.getName());
        taskPanelCategory.setSelectedItem(taskCurrent.getCategory());
        taskPanelDescription.setText(taskCurrent.getDescription());
        taskPanelDueDateModel.setValue(taskCurrent.getDue());
        taskPanelCompletionDateModel.setValue(taskCurrent.getCompleted());
        // Global UI update
        taskPanel.updateUI();
    }

    public void updateTaskTimes () {
        timeTableModel.setTimes(taskCurrent.getTimes());
        // Update tracking buttons
        TaskTime timeCurrent = taskCurrent.getTimeActive(false);
        if ((timeCurrent == null) || (timeCurrent.getStatus() == TaskTime.Status.STOPPED)) {
            // Stopped
            buttonsTimeTrack.setEnabled(true);
            buttonsTimePause.setEnabled(false);
            buttonsTimeStop.setEnabled(false);
        } else if (timeCurrent.getStatus() == TaskTime.Status.PAUSED) {
            // Paused
            buttonsTimeTrack.setEnabled(true);
            buttonsTimePause.setEnabled(false);
            buttonsTimeStop.setEnabled(true);
        } else {
            // Running
            buttonsTimeTrack.setEnabled(false);
            buttonsTimePause.setEnabled(true);
            buttonsTimeStop.setEnabled(true);
        }
        // Global UI update
        timePanel.updateUI();
    }

    public Boolean saveTask () {
        if (taskCurrent.getName().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type in a title before saving.", "Title missing!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (taskCurrent.getCategoryId().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type in or select a category before saving.", "Category missing!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Task originalData = new Task(taskCurrent);
        // Update task object
        taskCurrent.setName(taskPanelTitle.getText());
        if (taskPanelCategory.getSelectedItem() instanceof TaskCategory) {
            // Selected category
            taskCurrent.setCategory((TaskCategory) taskPanelCategory.getSelectedItem());
        } else {
            // Manual input / new
            String categoryName = (String) taskPanelCategory.getSelectedItem();
            TaskCategory category = new TaskCategory(categoryName);
            taskCurrent.setCategory(category);
        }
        taskCurrent.setDescription(taskPanelDescription.getText());
        taskCurrent.setDue(taskPanelDueDateModel.getValue());
        taskCurrent.setCompleted(taskPanelCompletionDateModel.getValue());
        taskCurrent.setDone(taskCurrent.getCompleted() != null);
        // Add to list (if not yet there)
        Vector<Task> taskList = TaskTracker.getInstance().getTasks();
        if (! taskList.contains(taskCurrent)) {
            // New task
            taskList.add(taskCurrent);
            TaskTracker.getInstance().updateTasks();
        }
        // Sync new/changed task
        taskCurrent.sync(originalData);
        // Clear dirty flag
        setDirty(false);
        // Call syncronisation listeners
        TaskListSyncronisationListener[] listeners = this.getListeners(TaskListSyncronisationListener.class);
        for (int listenerIndex = 0; listenerIndex < listeners.length; listenerIndex++) {
            listeners[listenerIndex].savedTask(taskCurrent);
            listeners[listenerIndex].selectTask(taskCurrent);
        }
        return true;
    }

    public void setDirty (Boolean dirty) {
        if (dirty) {
            taskCurrentEmpty = false;
            buttonsSave.setEnabled(true);
        } else {
            buttonsSave.setEnabled(false);
        }
        taskCurrentDirty = dirty;
    }

    public Vector<TaskCategory> getCategories () {
        return taskPanelCategoryList;
    }

    public void setCategories (Vector<TaskCategory> categoryList) {
        // Store current task state
        Boolean dirty = taskCurrentDirty;
        Boolean empty = taskCurrentEmpty;
        // Update category list
        taskPanelCategoryList.clear();
        taskPanelCategoryList.addAll(categoryList);
        if (! taskCurrent.getCategoryId().isEmpty()) {
            taskPanelCategory.setSelectedItem(taskCurrent.getCategory());
            taskPanelCategory.updateUI();
        }
        // Restore previous task state (prevent update affecting dirty/empty flag)
        taskCurrentDirty = dirty;
        taskCurrentEmpty = empty;
    }

    @Override
    public void itemStateChanged (ItemEvent event) {
        setDirty(true);
    }

    @Override
    public void changedUpdate (DocumentEvent event) {
        setDirty(true);
    }

    @Override
    public void insertUpdate (DocumentEvent event) {
        setDirty(true);
    }

    @Override
    public void removeUpdate (DocumentEvent event) {
        setDirty(true);
    }

    @Override
    public void stateChanged (ChangeEvent event) {
        setDirty(true);
    }
}