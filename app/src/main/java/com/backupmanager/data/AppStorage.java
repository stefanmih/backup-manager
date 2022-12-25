package com.backupmanager.data;

import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.app.utils.ListViewAdapterLocal;

public class AppStorage {
    public static ListViewAdapterDisk adapterRemote;
    public static ListViewAdapterLocal adapterLocal;
    public static String username;
    public static String password;
    public static final String baseUrl = "http://192.168.100.18:8080";
}
