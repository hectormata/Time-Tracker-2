package main.GUI.stage1.main;

import com.main.java.task.Settings.ConfigMain;
import com.main.java.task.Settings.ConfigSyncAccount;
import com.main.java.task.Synchronisation.Account;
import com.main.java.task.Synchronisation.GoogleTasks;
import com.main.java.task.Windows.MainWindow;
import com.main.java.task.Windows.SettingsWindow;
import com.main.java.task.Windows.TaskPanel;
import com.main.java.task.Task.*;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

public class TaskTracker implements ActionListener, MouseListener {

    private static TaskTracker instance = null;
    private MainWindow				windowMain;
    private TaskTime.Status         status;
    private SettingsWindow          windowSettings;
    private TrayIcon                trayIcon;
    private Image					trayIconImageDefault;
    private Image					trayIconImageTracking;
    private Image					trayIconImagePaused;
    private PopupMenu				trayIconMenu;
    private MenuItem				trayIconMenuSync;
    private MenuItem				trayIconMenuSettings;
    private MenuItem				trayIconMenuExit;
    private JFrame                  trayIconPopup;
    private TaskPanel               trayIconPopupTask;
    private ConfigMain              configuration;
    private File                    configurationFile;
    private File					categoryStoreFile;
    private Vector<TaskCategory>    categoryList;
    private File					taskStoreFile;
    private Vector<Task>			taskList;
    private Vector<Account>			accountList;

    // Work on login system
    // private static Logger logger = LoggerManager.getLogger(CLASSNAME);

    public TaskTracker(String[] args) {

        final String method = "TaskTracker";

        status = TaskTime.Status.STOPPED;

        // Load configs
        accountList = new Vector<Account>();
        configuration = new ConfigMain();
        configurationFile = new java.io.File( System.getProperty("user.home"), ".TimeTracker/configuration");
        loadConfiguration();
        // Load categories from file
        categoryStoreFile = new java.io.File( System.getProperty("user.home"), ".TimeTracker/categories");
        categoryList = new Vector<TaskCategory>();
        loadCategories();
        // Load tasks from file
        taskStoreFile = new java.io.File( System.getProperty("user.home"), ".TimeTracker/tasks");
        taskList = new Vector<Task>();
        loadTasks();
        // Init windows
        this.initMainWindow();
        this.initSettingsWindow();
        // Init system tray icon (if supported)
        trayIcon = null;
        trayIconImageDefault = null;
        trayIconImageTracking = null;
        trayIconImagePaused = null;
        trayIconMenu = null;
        trayIconPopup = null;
        if (SystemTray.isSupported()) {
            this.initSystemTrayIcon();
        }
    }

    private void initMainWindow() {

        windowMain = new MainWindow();
        windowMain.setMinimumSize(new Dimension(700, 480));
        windowMain.setSize(800, 600);
        windowMain.setLocationRelativeTo(null);
        windowMain.setCategories(categoryList);
        windowMain.setTasks(taskList);
    }

    private void initSettingsWindow() {
        windowSettings = new SettingsWindow();
        windowSettings.setMinimumSize(new Dimension(640, 480));
        windowSettings.setSize(640, 480);
        windowSettings.setLocationRelativeTo(null);
        windowSettings.setAccounts(accountList);
        windowSettings.setConfiguration(configuration);
    }

    private void initSystemTrayIcon() {
        try {
            trayIconImageDefault = Toolkit.getDefaultToolkit().getImage("images/taskbar.png");
            trayIconImageTracking = Toolkit.getDefaultToolkit().getImage("images/taskbar_tracking.png");
            trayIconImagePaused = Toolkit.getDefaultToolkit().getImage("images/taskbar_paused.png");
            trayIconMenu = new PopupMenu();
            trayIcon = new TrayIcon(trayIconImageDefault, "TimeTracker");
            trayIcon.setImageAutoSize(true);
            // Create tray icon menu
            trayIconMenuSync = new MenuItem("Sync");
            trayIconMenuSync.addActionListener(this);
            trayIconMenuSettings = new MenuItem("Settings");
            trayIconMenuSettings.addActionListener(this);
            trayIconMenuExit = new MenuItem("Exit");
            trayIconMenuExit.addActionListener(this);
            // - Add items to menu
            trayIconMenu.add(trayIconMenuSync);
            trayIconMenu.add(trayIconMenuSettings);
            trayIconMenu.add(trayIconMenuExit);
            // Create tray action popup
            trayIconPopup = new JFrame("TimeTracker");
            trayIconPopup.setUndecorated(true);
            trayIconPopup.setSize(700, 200);
            trayIconPopupTask = new TaskPanel();
            trayIconPopupTask.addSyncronisationListener(new TaskPanel.TaskListSyncronisationListener() {
                @Override
                public void clearSelection() {
                    // Not used
                }
                @Override
                public void discardedTask(Task task, Boolean previousWasEmpty) {
                    // Close when pressing "discard" on an empty task
                    if (previousWasEmpty) {
                        trayIconPopup.setVisible(false);
                    }
                }
                @Override
                public void selectTask(Task task) {
                    // Not used
                }
                @Override
                public void savedTask(Task task) {
                    // Close after saving
                    trayIconPopup.setVisible(false);
                }
                @Override
                public void pauseTracking(Task task) {
                    // Update status
                    TaskTracker.getInstance().setStatus(TaskTime.Status.PAUSED);
                    // Close after action
                    trayIconPopup.setVisible(false);
                }
                @Override
                public void startTracking(Task task) {
                    // Update status
                    TaskTracker.getInstance().setStatus(TaskTime.Status.TRACKING);
                    // Save task
                    trayIconPopupTask.saveTask();
                    // Close after action
                    trayIconPopup.setVisible(false);
                }
                @Override
                public void stopTracking(Task task) {
                    // Update status
                    TaskTracker.getInstance().setStatus(TaskTime.Status.STOPPED);
                    // Save task
                    trayIconPopupTask.saveTask();
                    // Close after action
                    trayIconPopup.setVisible(false);
                }
            });
            trayIconPopupTask.setBorder(BorderFactory.createEtchedBorder());
            trayIconPopup.add(trayIconPopupTask);
            // Bind popup menu
            trayIcon.setPopupMenu(trayIconMenu);
            // Bind action event (click)
            trayIcon.addActionListener(this);
            trayIcon.addMouseListener(this);
            // Add to system tray
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateSystemTrayIcon() {
        if (status == TaskTime.Status.STOPPED) {
            trayIcon.setImage(trayIconImageDefault);
        } else if (status == TaskTime.Status.TRACKING) {
            trayIcon.setImage(trayIconImageTracking);
        } else if (status == TaskTime.Status.PAUSED) {
            trayIcon.setImage(trayIconImagePaused);
        }
    }

    public void setStatus(TaskTime.Status newStatus) {
        status = newStatus;
        updateSystemTrayIcon();
    }

    public void displayMessage(String title, String content, TrayIcon.MessageType type) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, content, type);
        }
    }

    public void showMainWindow() {
        windowMain.setVisible(true);
        windowMain.updateTasks();
    }

    public void showSettingsWindow() {
        windowSettings.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    public void loadCategories() {
        if (categoryStoreFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(categoryStoreFile);
                ObjectInputStream stream = new ObjectInputStream(fileInputStream);
                categoryList = (Vector<TaskCategory>)stream.readObject();
                stream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void saveCategories() {
        try {
            FileOutputStream fileInputStream = new FileOutputStream(categoryStoreFile);
            ObjectOutputStream stream = new ObjectOutputStream(fileInputStream);
            stream.writeObject(categoryList);
            stream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateCategories() {
        // Update and save tasks
        windowMain.setCategories(categoryList);
        this.saveCategories();
    }

    @SuppressWarnings("unchecked")
    public void loadTasks() {
        if (taskStoreFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(taskStoreFile);
                ObjectInputStream stream = new ObjectInputStream(fileInputStream);
                taskList = (Vector<Task>)stream.readObject();
                stream.close();
                // Sort tasks by update date (newest first)
                Collections.sort(taskList, MainWindow.TASK_ORDER_UPDATED_DESC);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void saveTasks() {
        try {
            FileOutputStream fileInputStream = new FileOutputStream(taskStoreFile);
            ObjectOutputStream stream = new ObjectOutputStream(fileInputStream);
            stream.writeObject(taskList);
            stream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateTasks() {
        // Update and save tasks
        windowMain.setTasks(taskList);
        this.saveTasks();
    }

    public Vector<Task> getTasks() {
        return taskList;
    }

    public Boolean setTaskActive(Task activeTask) {
        if (windowMain.setTaskActive(activeTask)) {
            TaskTime timeCurrent = activeTask.getTimeActive(false);
            if (timeCurrent != null) {
                TaskTracker.getInstance().setStatus( timeCurrent.getStatus() );
            } else {
                TaskTracker.getInstance().setStatus( TaskTime.Status.STOPPED );
            }
            return trayIconPopupTask.editTask(activeTask, true);
        }
        return false;
    }

    public void syncTasks() {
        syncTasksReadCategories();
        syncTasksReadTasks();
        syncTasksWriteCategories();
        syncTasksWriteTasks();
    }

    public void syncTasksReadCategories() {
        Boolean					categoryNew;
        TaskCategory			categoryLocal;
        TaskCategory			categoryRemote;
        Vector<TaskCategory>	categoryListRemote;
        int						categoryCountNew = 0;
        int						categoryCountUpdated = 0;
        for (Iterator<Account> accountIt = accountList.iterator(); accountIt.hasNext(); ) {
            Account account = accountIt.next();
            categoryListRemote = account.getCategories();
            for (Iterator<TaskCategory> taskRemoteIt = categoryListRemote.iterator(); taskRemoteIt.hasNext(); ) {
                categoryNew = true;
                categoryRemote = taskRemoteIt.next();
                for (Iterator<TaskCategory> taskLocalIt = categoryList.iterator(); taskLocalIt.hasNext(); ) {
                    categoryLocal = taskLocalIt.next();
                    if (categoryLocal.isCategoryIdentical(categoryRemote)) {
                        // Task already known
                        categoryNew = false;
                        // Compare local and remote version
                        if (!categoryLocal.getUpdated().equals(categoryRemote.getUpdated())) {
                            if (categoryLocal.getUpdated().before(categoryRemote.getUpdated())) {
                                // Update local task
                                categoryLocal.setName( categoryRemote.getName() );
                                categoryLocal.setUpdated( categoryRemote.getUpdated() );
                                categoryCountUpdated++;
                            } else {
                                // Update remote task
                                account.updateCategory(categoryLocal);
                                categoryCountUpdated++;
                            }
                        }
                        break;
                    }
                }
                if (categoryNew) {
                    // New task, add to list
                    categoryList.add(categoryRemote);
                    categoryCountNew++;
                }
            }
        }
        if ((categoryCountUpdated > 0) || (categoryCountNew > 0)) {
            updateCategories();
            // Save syncronisation configuration changes
            saveConfiguration();
        }
    }

    public void syncTasksWriteCategories() {
        int				categoryCountCreate = 0;
        for (Iterator<Account> accountIt = accountList.iterator(); accountIt.hasNext(); ) {
            Account account = accountIt.next();
            for (Iterator<TaskCategory> taskIt = categoryList.iterator(); taskIt.hasNext(); ) {
                TaskCategory category = taskIt.next();
                if (category.getSyncId(account.getAccountIdent()) == null) {
                    // Task not present in current account, create!
                    account.createCategory(category);
                    categoryCountCreate++;
                }
            }
        }
        if (categoryCountCreate > 0) {
            // Update and save tasks
            windowMain.setTasks(taskList);
            this.saveTasks();
        }
    }

    public void syncTasksReadTasks() {
        Boolean					taskNew;
        Task					taskLocal;
        Task					taskRemote;
        Vector<Task>			taskListRemote;
        int						taskCountNew = 0;
        int						taskCountUpdated = 0;
        for (Iterator<Account> accountIt = accountList.iterator(); accountIt.hasNext(); ) {
            Account account = accountIt.next();
            taskListRemote = account.getTasks();
            for (Iterator<Task> taskRemoteIt = taskListRemote.iterator(); taskRemoteIt.hasNext(); ) {
                taskNew = true;
                taskRemote = taskRemoteIt.next();
                for (Iterator<Task> taskLocalIt = taskList.iterator(); taskLocalIt.hasNext(); ) {
                    taskLocal = taskLocalIt.next();
                    if (taskLocal.isTaskIdentical(taskRemote)) {
                        // Task already known
                        taskNew = false;
                        // Compare local and remote version
                        if (!taskLocal.getUpdated().equals(taskRemote.getUpdated())) {
                            if (taskLocal.getUpdated().before(taskRemote.getUpdated())) {
                                // Update local task
                                taskLocal.setDone( taskRemote.getDone() );
                                taskLocal.setHidden( taskRemote.getHidden() );
                                taskLocal.setDeleted( taskRemote.getDeleted() );
                                taskLocal.setName( taskRemote.getName() );
                                taskLocal.setCategory( taskRemote.getCategoryId() );
                                taskLocal.setDescription( taskRemote.getDescription() );
                                taskLocal.setDue( taskRemote.getDue() );
                                taskLocal.setUpdated( taskRemote.getUpdated() );
                                taskCountUpdated++;
                            } else {
                                // Update remote task
                                account.updateTask(taskLocal, taskRemote);
                                taskCountUpdated++;
                            }
                        }
                        break;
                    }
                }
                if (taskNew) {
                    // New task, add to list
                    taskList.add(taskRemote);
                    if (!taskRemote.getDeleted()) {
                        taskCountNew++;
                    }
                }
            }
        }
        if ((taskCountUpdated > 0) || (taskCountNew > 0)) {
            updateTasks();
            // Save syncronisation configuration changes
            saveConfiguration();
            // Display update notification
            displayMessage("Syncronized tasks", taskCountNew+" new tasks added, "+taskCountUpdated+" tasks updated.", TrayIcon.MessageType.INFO);
        }
    }

    public void syncTasksWriteTasks() {
        int				taskCountCreate = 0;
        for (Iterator<Account> accountIt = accountList.iterator(); accountIt.hasNext(); ) {
            Account account = accountIt.next();
            for (Iterator<Task> taskIt = taskList.iterator(); taskIt.hasNext(); ) {
                Task task = taskIt.next();
                if (task.getSyncId(account.getAccountIdent()) == null) {
                    // Task not present in current account, create!
                    account.createTask(task);
                    taskCountCreate++;
                }
            }
        }
        if (taskCountCreate > 0) {
            // Update and save tasks
            windowMain.setTasks(taskList);
            this.saveTasks();
        }
    }

    public TaskCategory getCategory(String categoryId) {
        for (Iterator<TaskCategory> iterator = categoryList.iterator(); iterator.hasNext(); ) {
            TaskCategory category = iterator.next();
            if (category.getId().equals(categoryId)) {
                return category;
            }
        }
        return null;
    }

    public Vector<TaskCategory> getCategories() {
        return categoryList;
    }

    public void loadConfiguration() {
        if (configurationFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(configurationFile);
                ObjectInputStream stream = new ObjectInputStream(fileInputStream);
                configuration = (ConfigMain) stream.readObject();
                accountList.clear();
                for (Iterator<ConfigSyncAccount> accountIt = configuration.syncAccounts.iterator(); accountIt.hasNext(); ) {
                    ConfigSyncAccount accountConfig = accountIt.next();
                    if (accountConfig.className.equals("GoogleTasks")) {
                        GoogleTasks account = new GoogleTasks(accountConfig.ident);
                        account.load(stream);
                        accountList.add(account);
                    }
                }
                stream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void saveConfiguration() {
        try {
            FileOutputStream fileInputStream = new FileOutputStream(configurationFile);
            ObjectOutputStream stream = new ObjectOutputStream(fileInputStream);
            configuration.syncAccounts.clear();
            for (Enumeration<Account> accountIt = accountList.elements(); accountIt.hasMoreElements(); ) {
                Account account = accountIt.nextElement();
                configuration.syncAccounts.add(new ConfigSyncAccount(account.getAccountIdent(), account.getType()));
            }
            stream.writeObject(configuration);
            for (Enumeration<Account> accountIt = accountList.elements(); accountIt.hasMoreElements(); ) {
                Account account = accountIt.nextElement();
                account.save(stream);
            }
            stream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addAccount(Account account) {
        accountList.add(account);
        configuration.syncAccounts.add(new ConfigSyncAccount(account.getAccountIdent(), account.getType()));
        saveConfiguration();
    }

    public Account getAccount(String accountIdent) {
        for (Iterator<Account> iterator = accountList.iterator(); iterator.hasNext(); ) {
            Account account = iterator.next();
            if (account.getAccountIdent().equals(accountIdent)) {
                // Create account object and return it
                return account;
            }
        }
        return null;
    }

    public Vector<Account> getAccounts() {
        return accountList;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(trayIcon)) {
            trayIconPopup.setVisible(false);
            this.showMainWindow();
        } else if (event.getSource().equals(trayIconMenuSync)) {
            this.syncTasks();
        } else if (event.getSource().equals(trayIconMenuSettings)) {
            this.showSettingsWindow();
        } else if (event.getSource().equals(trayIconMenuExit)) {
            if (windowMain.cleanup()) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            trayIconPopup.setLocationRelativeTo(null);
            trayIconPopup.setVisible( !trayIconPopup.isVisible() );
            if (trayIconPopup.isVisible()) {
                // Update edit panel
                trayIconPopupTask.updateTaskInputs();
                trayIconPopupTask.updateTaskTimes();
            }
        } else {
            trayIconPopup.setVisible(false);
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        // Nothing yet
    }

    @Override
    public void mouseExited(MouseEvent event) {
        // Nothing yet
    }

    @Override
    public void mousePressed(MouseEvent event) {
        // Nothing yet
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        // Nothing yet
    }

    public static TaskTracker getInstance() {

        if (instance == null) {
            instance = new TaskTracker(new String[]{});
        }

        return instance;
    }

    public static TaskTracker getInstance(String[] args) {

        if (instance == null) {
            instance = new TaskTracker(args);
        }

        return instance;
    }

    public static void main (String[] args) {

        TaskTracker ttInstance = TaskTracker.getInstance(args);
        // Show main window
        ttInstance.showMainWindow();
        // Synchronize tasks
        ttInstance.syncTasks();
        // Select newest task
        if (!ttInstance.getTasks().isEmpty()) {
            ttInstance.setTaskActive( ttInstance.getTasks().firstElement() );
        }
    }
}
