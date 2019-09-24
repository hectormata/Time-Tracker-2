package com.main.java.task.Synchronisation;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.api.services.tasks.Tasks.Tasklists;
import com.google.api.services.tasks.Tasks.TasksOperations;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.main.java.task.Settings.ConfigSyncAccountGoogleTasks;
import com.main.java.task.Task.Task;
import com.main.java.task.Task.TaskCategory;
import main.GUI.stage1.main.TaskTracker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import java.util.Vector;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GoogleTasks extends Account {

    private static final int MAX_TASKLISTS_PER_CALL = 100;
    /** Application name. */
    private static final String APPLICATION_NAME = "TimeTracker";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES = Arrays.asList(TasksScopes.TASKS, Oauth2Scopes.USERINFO_PROFILE);

    // Initialize http transport
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private JPanel 							panelConfig;
    private JButton 						buttonLogin;
    private JButton 						buttonLogout;
    private JButton 						buttonResync;
    private JLabel 							labelAccount;
    private File							googleDataStore;
    private FileDataStoreFactory			googleDataStoreFactory;
    private ConfigSyncAccountGoogleTasks	configuration;

    public GoogleTasks() throws IOException {
        super();
        panelConfig = null;
        configuration = new ConfigSyncAccountGoogleTasks();
        googleDataStore = new java.io.File( System.getProperty("user.home"), ".TimeTracker/"+accountIdent);
        googleDataStoreFactory = new FileDataStoreFactory(googleDataStore);
    }

    public GoogleTasks(String ident) throws IOException {
        super(ident);
        panelConfig = null;
        configuration = new ConfigSyncAccountGoogleTasks();
        googleDataStore = new java.io.File( System.getProperty("user.home"), ".TimeTracker/"+accountIdent);
        googleDataStoreFactory = new FileDataStoreFactory(googleDataStore);
    }


    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = GoogleTasks.class.getResourceAsStream("/TimeTracker/Syncronisation/GoogleTasksSecret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(googleDataStoreFactory)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + googleDataStore.getAbsolutePath());
        this.checkAuthorisation(credential);
        return credential;
    }

    private void checkAuthorisation(Credential credential) throws IOException {
        initConfiguration();
        File fileCredential = new File(googleDataStore, "StoredCredential");
        buttonLogin.setEnabled(false);
        buttonLogout.setEnabled(false);
        buttonResync.setEnabled(false);
        if (fileCredential.exists()) {
            if (credential == null) {
                credential = authorize();
            }
            Oauth2 oauth = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            Userinfoplus userinfoplus = oauth.userinfo().get().execute();
            labelAccount.setText("Account: "+userinfoplus.getName());
            buttonLogout.setEnabled(true);
            buttonResync.setEnabled(true);
        } else {
            labelAccount.setText("Account: -- not logged in --");
            buttonLogin.setEnabled(true);
        }
    }

    private void logout() throws IOException {
        File fileCredential = new File(googleDataStore, "StoredCredential");
        if (fileCredential.exists()) {
            fileCredential.delete();
            googleDataStore.delete();
            googleDataStoreFactory = new FileDataStoreFactory(googleDataStore);
        }
        this.checkAuthorisation(null);
    }

    /**
     * Build and return an authorized Tasks client service.
     * @return an authorized Tasks client service
     * @throws IOException
     */
    private Tasks getTasksService() throws IOException {
        Credential credential = authorize();
        return new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void initConfiguration() {
        if (panelConfig != null) {
            return;
        }
        // Create elements
        panelConfig = new JPanel();
        labelAccount = new JLabel("Account: -- not logged in --");
        buttonLogin = new JButton("Login");
        buttonLogout = new JButton("Logout");
        buttonResync = new JButton("Syncronize all tasks");
        JLabel			labelTitle = new JLabel("Google Tasks Account Settings");
        labelTitle.setFont(new Font("Arial", Font.BOLD, 20));
        // Setup layout
        GroupLayout 	layout = new GroupLayout(panelConfig);
        panelConfig.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(labelTitle)
                        .addGroup( layout.createSequentialGroup().addComponent(labelAccount) )
                        .addGroup( layout.createSequentialGroup().addComponent(buttonLogin).addComponent(buttonLogout).addComponent(buttonResync) )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(labelTitle)
                        .addComponent(labelAccount)
                        .addGroup( layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(buttonLogin).addComponent(buttonLogout).addComponent(buttonResync) )
        );
        // Bind events
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    GoogleTasks.this.authorize();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        buttonLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    GoogleTasks.this.logout();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        buttonResync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GoogleTasks.this.configuration.lastUpdate = null;
                TaskTracker.getInstance().syncTasks();
            }
        });
        // Check account state
        try {
            this.checkAuthorisation(null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Google Tasks";
    }

    @Override
    public String getType() {
        return "GoogleTasks";
    }

    @Override
    public Vector<TaskCategory> getCategories(Vector<TaskCategory> categoryList) {
        try {
            Tasklists.List request = getTasksService().tasklists().list().setMaxResults( Long.valueOf(MAX_TASKLISTS_PER_CALL) );
            TaskLists result = request.execute();
            while (result.getItems().size() > 0) {
                for (Iterator<TaskList> iterator = result.getItems().iterator(); iterator.hasNext(); ) {
                    TaskList current = iterator.next();
                    Date dateUpdate = (current.getUpdated() == null ? null : new Date(current.getUpdated().getValue()) );
                    TaskCategory category = new TaskCategory(current.getTitle(), dateUpdate, accountIdent, current.getId());
                    if (!category.isCategoryWithinList(categoryList)) {
                        categoryList.add(category);
                    }
                }
                if (result.getNextPageToken() == null) {
                    break;
                }
                request.setPageToken( result.getNextPageToken() );
                result = request.execute();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return categoryList;
    }

    @Override
    public void createCategory(TaskCategory category) {
        try {
            com.google.api.services.tasks.model.TaskList taskListRemote = new com.google.api.services.tasks.model.TaskList();
            // - Set update date
            String dateUpdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
            taskListRemote.setUpdated(DateTime.parseRfc3339(dateUpdate));
            // - Set title
            taskListRemote.setTitle( category.getName() );
            // Create remote task
            Tasklists.Insert	request = getTasksService().tasklists().insert(taskListRemote);
            taskListRemote = request.execute();
            // Add sync id
            category.getSyncIds().put(accountIdent, taskListRemote.getId());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCategory(TaskCategory category) {
        try {
            Tasklists.Delete	request = getTasksService().tasklists().delete( category.getSyncId(accountIdent) );
            request.execute();
            category.getSyncIds().remove(accountIdent);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void updateCategory(TaskCategory category) {
        try {
            String taskListId = category.getSyncId(accountIdent);
            // Get current task state and update all supported fields
            Tasklists.Get		requestGet = getTasksService().tasklists().get(taskListId);
            com.google.api.services.tasks.model.TaskList taskListCurrent = requestGet.execute();
            // - Update date
            String dateUpdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
            taskListCurrent.setUpdated(DateTime.parseRfc3339(dateUpdate));
            // - Update title
            taskListCurrent.setTitle( category.getName() );
            // Update changed task
            Tasklists.Update	requestUpdate = getTasksService().tasklists().update(taskListId, taskListCurrent);
            requestUpdate.execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Vector<Task> getTasks() {
        return this.getTasks(configuration.lastUpdate);
    }

    @Override
    public Vector<Task> getTasks(Date updatedMin) {
        Vector<TaskCategory>	categories = this.getCategories( TaskTracker.getInstance().getCategories() );
        Vector<Task>			tasks = new Vector<Task>();
        try {
            for (Iterator<TaskCategory> iterator = categories.iterator(); iterator.hasNext(); ) {
                TaskCategory taskList = iterator.next();
                TasksOperations.List	request = getTasksService().tasks().list(taskList.getSyncId(accountIdent));
                request.setShowHidden(true).setShowDeleted(true);
                if (updatedMin != null) {
                    String updatedMinStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(updatedMin);
                    request.setUpdatedMin(updatedMinStr);
                }
                com.google.api.services.tasks.model.Tasks result = request.execute();
                while ((result.getItems() != null) && (result.getItems().size() > 0)) {
                    for (Iterator<com.google.api.services.tasks.model.Task> itTask = result.getItems().iterator(); itTask.hasNext(); ) {
                        com.google.api.services.tasks.model.Task current = itTask.next();
                        Date dateUpdate = (current.getUpdated() == null ? null : new Date(current.getUpdated().getValue()) );
                        Date dateDue = (current.getDue() == null ? null : new Date(current.getDue().getValue()) );
                        Date dateCompleted = (current.getCompleted() == null ? null : new Date(current.getCompleted().getValue()) );
                        tasks.add(new Task(
                                current.getStatus().equals("completed"), current.getHidden(), current.getDeleted(),
                                current.getTitle(), taskList.getId(), current.getNotes(), dateUpdate, dateUpdate, dateDue, dateCompleted,
                                accountIdent, current.getId()
                        ));
                    }
                    if (result.getNextPageToken() == null) {
                        break;
                    }
                    request.setPageToken( result.getNextPageToken() );
                    result = request.execute();
                }
            }
            // Update stamp
            configuration.lastUpdate = new Date();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public void createTask(Task task) {
        if (task.getCategory().getSyncId(accountIdent) == null) {
            // Create category!
            createCategory( task.getCategory() );
            TaskTracker.getInstance().saveCategories();
        }
        try {
            com.google.api.services.tasks.model.Task taskRemote = new com.google.api.services.tasks.model.Task();
            // - Set dates
            String dateCompleted = (task.getCompleted() == null ? null : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(task.getCompleted()));
            String dateDue = (task.getDue() == null ? null : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(task.getDue()));
            String dateUpdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
            taskRemote
                    .setCompleted((dateCompleted == null ? null : DateTime.parseRfc3339(dateCompleted)))
                    .setDue((dateDue == null ? null : DateTime.parseRfc3339(dateDue)))
                    .setUpdated(DateTime.parseRfc3339(dateUpdate));
            // - Set title
            taskRemote.setTitle( task.getName() );
            // - Set description / nots
            taskRemote.setNotes( task.getDescription() );
            // - Set status
            taskRemote.setStatus( (task.getDone() ? "completed" : "needsAction") );
            taskRemote.setHidden( task.getHidden() );
            taskRemote.setDeleted( task.getDeleted() );
            // Create remote task
            TasksOperations.Insert	request = getTasksService().tasks().insert(task.getCategory().getSyncId(accountIdent), taskRemote);
            taskRemote = request.execute();
            // Add sync id
            task.getSyncIds().put(accountIdent, taskRemote.getId());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTask(Task task) {
        try {
            TasksOperations.Delete	request = getTasksService().tasks().delete( task.getCategory().getSyncId(accountIdent), task.getSyncId(accountIdent) );
            request.execute();
            task.getSyncIds().remove(accountIdent);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void updateTask(Task task, Task originalData) {
        if (task.getCategory().getSyncId(accountIdent) == null) {
            // Create category!
            createCategory( task.getCategory() );
            TaskTracker.getInstance().saveCategories();
        }
        try {
            String taskId = originalData.getSyncId(accountIdent), taskListId = originalData.getCategory().getSyncId(accountIdent);
            // Get current task state and update all supported fields
            TasksOperations.Get		requestGet = getTasksService().tasks().get(taskListId, taskId);
            com.google.api.services.tasks.model.Task taskCurrent = requestGet.execute();
            // Move to another task list?
            if (!originalData.getCategoryId().equals(task.getCategoryId())) {
                // Recreate task
                deleteTask(originalData);
                // Create remote task
                taskCurrent.setId(null);
                TasksOperations.Insert	request = getTasksService().tasks().insert(task.getCategory().getSyncId(accountIdent), taskCurrent);
                taskCurrent = request.execute();
                // Add sync id
                task.getSyncIds().put(accountIdent, taskCurrent.getId());
            } else {
                // - Update dates
                String dateCompleted = (task.getCompleted() == null ? null : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(task.getCompleted()));
                String dateDue = (task.getDue() == null ? null : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(task.getDue()));
                String dateUpdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
                taskCurrent
                        .setCompleted((dateCompleted == null ? null : DateTime.parseRfc3339(dateCompleted)))
                        .setDue((dateDue == null ? null : DateTime.parseRfc3339(dateDue)))
                        .setUpdated(DateTime.parseRfc3339(dateUpdate));
                // - Update title
                taskCurrent.setTitle( task.getName() );
                // - Set description / nots
                taskCurrent.setNotes( task.getDescription() );
                // - Update status
                taskCurrent.setStatus( (task.getDone() ? "completed" : "needsAction") );
                taskCurrent.setHidden( task.getHidden() );
                taskCurrent.setDeleted( task.getDeleted() );
                // Update changed task
                TasksOperations.Update	requestUpdate = getTasksService().tasks().update(taskListId, taskId, taskCurrent);
                requestUpdate.execute();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public JPanel getConfiguration() {
        if (panelConfig == null) {
            this.initConfiguration();
        }
        return panelConfig;
    }

    @Override
    public void load(InputStream stream) {
        try {
            ObjectInputStream objStream = new ObjectInputStream(stream);
            configuration = (ConfigSyncAccountGoogleTasks) objStream.readObject();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void save(OutputStream stream) {
        try {
            ObjectOutputStream objStream = new ObjectOutputStream(stream);
            objStream.writeObject(configuration);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}