package com.backupmanager.data;

import android.app.Activity;
import android.widget.ProgressBar;

import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.app.utils.ListViewAdapterLocal;

public class AppStorage {
    public static ListViewAdapterDisk adapterRemote;
    public static ListViewAdapterLocal adapterLocal;
    public static String username;
    public static String password;
    public static ProgressBar progressBarLocal;
    public static Activity activity;
    public static String baseUrl;
    public static String ip;
    public static String httpPort;
    public static String tcpPort;
}
