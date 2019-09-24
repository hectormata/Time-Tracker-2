package com.main.java.task.Settings;

import java.io.Serializable;
import java.util.Date;

public class ConfigSyncAccountGoogleTasks implements Serializable {

    private static final long serialVersionUID = 2L;
    public Boolean	enabled;
    public Date lastUpdate;

    public ConfigSyncAccountGoogleTasks() {
        enabled = true;
        lastUpdate = null;
    }
}