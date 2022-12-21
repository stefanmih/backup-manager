package com.backupmanager.api;

import android.os.AsyncTask;
import android.util.Base64;

import com.backupmanager.data.AppStorage;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class DirectoriesAPI extends AsyncTask<URL, String , JSONArray> {

    public static String subPath = "";
    private String username;
    private String password;

    public DirectoriesAPI(String subPath, String username, String password){
       this.subPath += (subPath + "%5C");
       this.username = username;
       this.password = password;
    }

    public abstract void onFinishLoading(JSONArray result);

    @Override
    protected JSONArray doInBackground(URL... urls) {
        try {
            URL url = new URL(AppStorage.baseUrl + "/files?path=" + subPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization" , "Basic " + new String(Base64.encode((username + ":" + password).getBytes(StandardCharsets.UTF_8), 0)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String row;
            String response = "";
            while((row = reader.readLine()) != null){
                response += (row + "\n");
            }
            reader.close();
            connection.disconnect();
            return new JSONArray(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray s) {
        super.onPostExecute(s);
        onFinishLoading(s);
    }
}
