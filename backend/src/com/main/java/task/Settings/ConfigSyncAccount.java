package com.main.java.task.Settings;

import java.io.Serializable;

public class ConfigSyncAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    public String	ident;
    public String	className;

    public ConfigSyncAccount(String ident, String className) {
        this.ident = ident;
        this.className = className;
    }
}