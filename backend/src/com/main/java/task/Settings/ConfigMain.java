package com.main.java.task.Settings;

import java.io.Serializable;
import java.util.Vector;

public class ConfigMain implements Serializable {

    private static final long serialVersionUID = 1L;
    public Vector<ConfigSyncAccount> syncAccounts;

    public ConfigMain () {
        syncAccounts = new Vector<ConfigSyncAccount>();
    }
}