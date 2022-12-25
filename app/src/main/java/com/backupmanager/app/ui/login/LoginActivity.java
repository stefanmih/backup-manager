package com.backupmanager.app.ui.login;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backupmanager.app.BuildConfig;
import com.backupmanager.app.MainPageActivity;
import com.backupmanager.app.R;
import com.backupmanager.app.databinding.ActivityLoginBinding;
import com.backupmanager.app.utils.Configuration;
import com.backupmanager.data.AppStorage;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button loginButton = binding.login;
        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.login.setEnabled(charSequence.length() > 5);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        requestPermission();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(((TextView)findViewById(R.id.username)).getText().toString(), ((TextView)findViewById(R.id.password)).getText().toString());
            }
        });
        checkAutoLogin();
    }

    private void requestPermission(){
        if(SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                intentActivityResultLauncher.launch(intent);
            }catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intentActivityResultLauncher.launch(intent);
                e.printStackTrace();
            }
        }
    }

    private ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
        }
    });

    private void login(String username, String password){
        @SuppressLint("StaticFieldLeak") final AsyncTask<URL, String, String> task = new AsyncTask<URL, String, String>(){
            @Override
            protected String doInBackground(URL... urls) {
                try{
                    URL url = new URL(AppStorage.baseUrl + "/login");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization" , "Basic " + new String(Base64.encode((username + ":" + password).getBytes(StandardCharsets.UTF_8), 0)));
                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        Intent mainPage = new Intent(LoginActivity.this, MainPageActivity.class);
                        mainPage.putExtra("USERNAME", username);
                        mainPage.putExtra("PASSWORD", password);
                        AppStorage.username = username;
                        AppStorage.password = password;
                        startActivity(mainPage);
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLoginFailed(R.string.login_failed);
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    private void checkAutoLogin() {
        if(Configuration.getConfiguration(getApplicationContext()).getParameterValue("autologin").equals("true")){
            login(Configuration.getConfiguration(getApplicationContext()).getParameterValue("username"), Configuration.getConfiguration(getApplicationContext()).getParameterValue("password"));
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}