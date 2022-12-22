package com.backupmanager.app.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

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

import com.backupmanager.app.MainPageActivity;
import com.backupmanager.app.R;
import com.backupmanager.app.ui.login.LoginViewModel;
import com.backupmanager.app.ui.login.LoginViewModelFactory;
import com.backupmanager.app.databinding.ActivityLoginBinding;
import com.backupmanager.app.utils.Configuration;
import com.backupmanager.data.AppStorage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login(((TextView)findViewById(R.id.username)).getText().toString(), ((TextView)findViewById(R.id.password)).getText().toString());
            }
        });
        checkAutoLogin();
    }

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