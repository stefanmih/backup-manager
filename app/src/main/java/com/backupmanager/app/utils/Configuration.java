package com.backupmanager.app.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private static Configuration configuration;

    private Context context;
    private File confFile;
    private Map<String, String> configurationMap = new HashMap<>();

    public Configuration(Context context) {
        this.context = context;
        confFile = new File(context.getFilesDir().getAbsolutePath(), "configuration.cfg");
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

    private synchronized void generateFile() {
        try {
            confFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void writeToFile(String content) {
        configurationMap.put(content.split("=")[0].trim(), content.split("=")[1].trim());
        writeToFile();
    }

    private synchronized void writeToFile() {
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

    private synchronized Map<String, String> readConfiguration() {
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

    public synchronized void clearConfiguration() {
        try {
            confFile.delete();
            generateFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addOrModifyConfiguration(String parameter, String value) {
        writeToFile(parameter + "=" + value + "\n");
    }

    public synchronized String getParameterValue(String parameter){
        return configurationMap.get(parameter).trim();
    }

    public synchronized Map<String, String> getConfiguration(){
        return configurationMap;
    }

    public static Configuration getConfiguration(Context context) {
        if (configuration == null) {
            configuration = new Configuration(context);
        }
        return configuration;
    }
}
