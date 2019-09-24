package com.main.java.task.Windows;

import com.main.java.task.Settings.ConfigMain;
import com.main.java.task.Synchronisation.Account;
import com.main.java.task.Synchronisation.GoogleTasks;
import main.GUI.stage1.main.TaskTracker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class SettingsWindow extends JFrame implements ActionListener, ListSelectionListener, WindowListener {

    private static final long serialVersionUID = 1L;
    private JTabbedPane             tabbedPane;
    private JPanel                  tabPanelGeneral;
    private JPanel                  tabPanelSynchronisation;
    private JPanel						syncPanelLeft;
    private JPanel						syncPanelCenter;
    private JList<Account>				syncListAccount;
    private DefaultListModel<Account>	syncListModelAccount;
    private JButton						syncButtonAdd;
    private JPopupMenu					syncMenuAdd;
    private JMenuItem					syncMenuAddGoogleTasks;
    private ConfigMain configuration;

    public SettingsWindow() {

        super("Settings");
        configuration = new ConfigMain();
        this.initTabGeneral();
        this.initTabSynchronisation();
        tabbedPane = new JTabbedPane();
        // TODO tabbedPane.addTab("General", tabPaneGeneral);
        tabbedPane.addTab("Synchronisation", tabPanelSynchronisation);
        this.add(tabbedPane);
        this.addWindowListener(this);

    }

    private void initTabGeneral() {

        tabPanelGeneral = new JPanel();
    }

    private void initTabSynchronisation() {

        // Left
        syncListModelAccount = new DefaultListModel<Account>();
        syncListAccount = new JList<Account>(syncListModelAccount);
        syncListAccount.addListSelectionListener(this);
        syncButtonAdd = new JButton("Add new ...");
        syncButtonAdd.addActionListener(this);
        syncMenuAddGoogleTasks = new JMenuItem("Google Tasks");
        syncMenuAddGoogleTasks.addActionListener(this);
        syncMenuAdd = new JPopupMenu();
        syncMenuAdd.setInvoker(syncButtonAdd);
        syncMenuAdd.add(syncMenuAddGoogleTasks);
        syncPanelLeft = new JPanel(new BorderLayout());
        syncPanelLeft.add(syncListAccount, BorderLayout.CENTER);
        syncPanelLeft.add(syncButtonAdd, BorderLayout.SOUTH);
        // Center
        syncPanelCenter = new JPanel(new BorderLayout());
        // Main panel
        tabPanelSynchronisation = new JPanel(new BorderLayout());
        tabPanelSynchronisation.add(syncPanelLeft, BorderLayout.WEST);
        tabPanelSynchronisation.add(syncPanelCenter, BorderLayout.CENTER);
    }

    public ConfigMain getConfiguration() {

        return configuration;
    }

    public void setAccounts(Vector<Account> accountList) {
        syncListModelAccount.removeAllElements();
        for (Iterator<Account> accountIt = accountList.iterator(); accountIt.hasNext(); ) {
            syncListModelAccount.addElement(accountIt.next());
        }
    }

    public void setConfiguration(ConfigMain config) {

        configuration = config;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(syncMenuAddGoogleTasks)) {
            try {
                GoogleTasks account = new GoogleTasks();
                TaskTracker.getInstance().addAccount(account);
                syncListModelAccount.addElement(account);
                syncPanelCenter.removeAll();
                syncPanelCenter.add(account.getConfiguration(), BorderLayout.CENTER);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (event.getSource().equals(syncButtonAdd)) {
            syncMenuAdd.show(syncButtonAdd, 10, 10);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        if (event.getSource().equals(syncListAccount)) {
            Account account = syncListAccount.getSelectedValue();
            if (account != null) {
                syncPanelCenter.removeAll();
                syncPanelCenter.add(account.getConfiguration(), BorderLayout.CENTER);
            }
        }
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
        // Nothing yet
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        // Nothing yet
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        TaskTracker.getInstance(new String[]{}).syncTasks();
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
        // Nothing yet
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
        // Nothing yet
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
        // Nothing yet
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
        // Nothing yet
    }
}
