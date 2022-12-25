package com.backupmanager.api;

import static com.backupmanager.data.AppStorage.password;
import static com.backupmanager.data.AppStorage.username;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Base64;

import com.backupmanager.data.AppStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BackupAPI {

    private List<String> paths = new ArrayList<>();
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public BackupAPI() {
    }

    @SuppressLint("StaticFieldLeak")
    public static void requestFilesUpload(List<String> paths){
        StringBuilder pathString = new StringBuilder();
        for(String s : paths){
            pathString.append(s.replace("/storage/emulated/0/", "")).append("%0A");
        }
        new AsyncTask<URL, String, String >(){
            @Override
            protected String doInBackground(URL... urls) {
                try{
                    URL url = new URL(AppStorage.baseUrl + "/requestFilesUpload?paths=" + pathString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization" , "Basic " + new String(Base64.encode((username + ":" + password).getBytes(StandardCharsets.UTF_8), 0)));
                    System.out.println("CODE " + connection.getResponseCode());
                    connection.disconnect();
                    uploadFiles(paths);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static void uploadFiles(List<String> paths){
        new AsyncTask<URL, String, String >(){
            @Override
            protected String doInBackground(URL... urls) {
                for(String s : paths){
                    uploadFile(s);
                }
                return null;
            }
        }.execute();
    }

    public static void uploadFile(String path) {
        try(Socket socket = new Socket("192.168.100.18",5000)) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            sendFile(path);
            dataInputStream.close();
            dataOutputStream.close();
        }catch (Exception e){
            uploadFile(path);
        }
    }

    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}
