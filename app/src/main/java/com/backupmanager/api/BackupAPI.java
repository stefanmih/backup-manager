package com.backupmanager.api;

import static com.backupmanager.data.AppStorage.baseUrl;
import static com.backupmanager.data.AppStorage.password;
import static com.backupmanager.data.AppStorage.username;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.backupmanager.app.R;
import com.backupmanager.data.AppStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public abstract class BackupAPI {

    private List<String> paths = new ArrayList<>();
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public BackupAPI() {
    }

    @SuppressLint("StaticFieldLeak")
    public static void requestFilesUpload(List<String> paths) {
        new AsyncTask<URL, String, String>() {
            @Override
            protected String doInBackground(URL... urls) {
                try {
                    URL url = new URL(AppStorage.baseUrl + "/requestFilesUpload");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setRequestProperty("Authorization", "Basic " + new String(Base64.encode((username + ":" + password).getBytes(StandardCharsets.UTF_8), 0)));
                    try (OutputStream os = connection.getOutputStream()) {
                        for (int i = 0; i < paths.size(); i++) {
                            byte[] input = (paths.get(i).replace("/storage/emulated/0/", "") + ";").getBytes(StandardCharsets.UTF_8);
                            System.out.println(paths.get(i));
                            os.write(input);

                        }
                    }
                    System.out.println(connection.getResponseCode());
                    if (connection.getResponseCode() == 200) {
                        uploadFiles(paths);
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static void uploadFiles(List<String> paths) {
        new AsyncTask<URL, String, String>() {
            @Override
            protected String doInBackground(URL... urls) {
                int i = 0;
                AppStorage.activity.runOnUiThread(() -> {
                    AppStorage.progressBarLocal.setVisibility(View.VISIBLE);
                    AppStorage.activity.findViewById(R.id.localList).setEnabled(false);
                });
                for (String s : paths) {
                    if (!s.trim().isEmpty()) {
                        uploadFile(s);
                        i++;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            int finalI = i;
                            AppStorage.activity.runOnUiThread(() -> AppStorage.progressBarLocal.setProgress((int) (((double) finalI / paths.size()) * 100), true));
                        }
                    }
                }
                AppStorage.activity.runOnUiThread(() -> {
                    AppStorage.progressBarLocal.setVisibility(View.INVISIBLE);
                    AppStorage.activity.findViewById(R.id.localList).setEnabled(true);
                });
                return null;
            }
        }.execute();
    }

    public static void uploadFile(String path) {
        try (Socket socket = new Socket(AppStorage.ip, Integer.parseInt(AppStorage.tcpPort))) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            sendFile(path);
            socket.getOutputStream().flush();
            dataInputStream.close();
            dataOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(String path) throws Exception {
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

    @SuppressLint("StaticFieldLeak")
    public static void downloadFile(String path) {
        new AsyncTask<URL, String, String>() {
            @Override
            protected String doInBackground(URL... urls) {
                try {
                    String pathEnc = path.replace("\\", "%5C");
                    URL url = new URL(baseUrl + "/download?path=" + pathEnc);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setRequestProperty("Authorization", "Basic " + new String(Base64.encode((username + ":" + password).getBytes(StandardCharsets.UTF_8), 0)));
                    AppStorage.activity.runOnUiThread(() -> {
                        AppStorage.activity.findViewById(R.id.progressBar3).setVisibility(View.VISIBLE);
                        Toast.makeText(AppStorage.activity, "Starting downlaod...", Toast.LENGTH_SHORT).show();
                    });
                    try (InputStream is = connection.getInputStream()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            String result = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining());
                            FileOutputStream writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Download/" + path.substring(path.lastIndexOf("\\") + 1));
                            List<Byte> lb = Arrays.stream(result.replace("[", "").replace("]", "").split(", ")).map(e -> ((byte) Integer.parseInt(e))).collect(Collectors.toList());
                            for (Byte b : lb) {
                                writer.write(b);
                            }
                            writer.close();
                        }
                    }
                    AppStorage.activity.runOnUiThread(() -> {
                        AppStorage.activity.findViewById(R.id.progressBar3).setVisibility(View.INVISIBLE);
                        Toast.makeText(AppStorage.activity, "Download finished.", Toast.LENGTH_SHORT).show();
                    });
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
