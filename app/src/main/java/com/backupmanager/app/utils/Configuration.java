package com.backupmanager.app.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

    private static Configuration configuration;

    private Context context;
    private File confFile;
    private File backupFile;
    private Map<String, String> configurationMap = new HashMap<>();

    public Configuration(Context context) {
        this.context = context;
        confFile = new File(context.getFilesDir().getAbsolutePath(), "configuration.cfg");
        backupFile = new File(context.getFilesDir().getAbsolutePath(), "backupfiles.cfg");
        if (!backupFile.exists()) {
            generateBackupFile();
        }
        if (!confFile.exists()) {
            generateFile();
        }else if(confFile.length() < 5){
            configurationMap.put("autologin", "true");
            configurationMap.put("username", "admin");
            configurationMap.put("password", "admin");
            writeToFile();
        }else{
            configurationMap = readConfiguration();
        }
    }

    private void generateBackupFile() {
        try {
            backupFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateFile() {
        try {
            confFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String content) {
        configurationMap.put(content.split("=")[0].trim(), content.split("=")[1].trim());
        writeToFile();
    }

    private void writeToFile() {
        try {
            clearConfiguration();
            FileWriter writer = new FileWriter(confFile, true);
            for (Map.Entry<String, String> entry : configurationMap.entrySet()) {
                writer.append(entry.getKey().trim() + "=" + entry.getValue().trim() + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> readConfiguration() {
        BufferedReader reader;
        Map<String, String> confMap = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(confFile));
            String line = reader.readLine();
            while (line != null) {
                confMap.put(line.split("=")[0], line.split("=")[1]);
                line = reader.readLine();
            }
            reader.close();
            return confMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public void clearConfiguration() {
        try {
            confFile.delete();
            generateFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearBackup() {
        try {
            backupFile.delete();
            generateFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOrModifyConfiguration(String parameter, String value) {
        writeToFile(parameter + "=" + value + "\n");
    }

    public String getParameterValue(String parameter){
        return configurationMap.get(parameter).trim();
    }

    public static Configuration getConfiguration(Context context) {
        if (configuration == null) {
            configuration = new Configuration(context);
        }
        return configuration;
    }

    public  List<String> getFilesForBackup(){
        List<String> list = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(backupFile));
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addFileForBackup(String path) {
        try {
            FileWriter writer = new FileWriter(backupFile, true);
            writer.append(path).append("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFileFromBackup(String path) {
        try {
            List<String> list = getFilesForBackup();
            clearBackup();
            generateBackupFile();
            FileWriter writer = new FileWriter(backupFile, true);
            for (String s : list) {
                if(!s.trim().equals(path.trim()))
                    writer.append(s).append("\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
