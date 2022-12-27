package com.backupmanager.data;

import android.content.Context;
import android.os.Environment;

import com.backupmanager.app.utils.Configuration;
import com.backupmanager.app.utils.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LocalFiles {

    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static Context context;

    public static List<File> getFiles(String folderName){
        path += (folderName + "/");
        List<File> files = new ArrayList<>();
        for(java.io.File f : Objects.requireNonNull(new java.io.File(path).listFiles())){
            File file = new File();
            file.setName(f.getName());
            file.setDirectory(f.isDirectory());
            file.setSize(f.length());
            file.setLocalPath(f.getAbsolutePath());
            file.setBackup(Configuration.getConfiguration(context).getFilesForBackup().contains(f.getAbsolutePath()) || Configuration.getConfiguration(context).getFilesForBackup().contains(Objects.requireNonNull(f.getParentFile()).getAbsolutePath()));
            files.add(file);
        }
        return files;
    }
}
