package com.backupmanager.data;

import com.backupmanager.app.utils.ListViewAdapter;

public class AppStorage {
    public static ListViewAdapter adapterRemote;
    public static ListViewAdapter adapterLocal;
    public static final String baseUrl = "http://192.168.100.18:8080";
    public static boolean autoLogin = false;
}
