package com.backupmanager.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.backupmanager.api.DirectoriesAPI;
import com.backupmanager.app.ui.login.LoginActivity;
import com.backupmanager.app.utils.Configuration;
import com.backupmanager.app.utils.File;
import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.app.utils.ListViewAdapterLocal;
import com.backupmanager.data.AppStorage;
import com.backupmanager.data.LocalFiles;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathUtils;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.backupmanager.app.databinding.ActivityMainPageBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainPageBinding binding;
    private Bundle mainPageBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPageBundle = savedInstanceState;

        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainPage.toolbar);
        binding.appBarMainPage.fab.setOnClickListener(view -> backup());
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_disk, R.id.nav_settings, R.id.nav_local_files, R.id.logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        getFilesFromRepository("");

    }

    private void backup() {
    }


    @Override
    public void onBackPressed() {
        if (findViewById(R.id.localList) != null && findViewById(R.id.localList).getVisibility() == View.VISIBLE) {
            LocalFiles.path = LocalFiles.path.substring(0, LocalFiles.path.lastIndexOf("/"));
            LocalFiles.path = LocalFiles.path.substring(0, LocalFiles.path.lastIndexOf("/"));
            AppStorage.adapterLocal = new ListViewAdapterLocal(MainPageActivity.this, LocalFiles.getFiles(""));
            ((ListView)findViewById(R.id.localList)).setAdapter(AppStorage.adapterLocal);
        }else {
            if (DirectoriesAPI.subPath.equals("%5C")) {
                DirectoriesAPI.subPath = "";
                MainPageActivity.this.finishAndRemoveTask();
                MainPageActivity.this.finishAffinity();
            } else {
                DirectoriesAPI.subPath = DirectoriesAPI.subPath.substring(0, DirectoriesAPI.subPath.length() - 4);
                DirectoriesAPI.subPath = DirectoriesAPI.subPath.substring(0, DirectoriesAPI.subPath.lastIndexOf("%5C"));
                getFilesFromRepository("");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getFilesFromRepository(String subPath) {
        new DirectoriesAPI(subPath, getIntent().getStringExtra("USERNAME"), getIntent().getStringExtra("PASSWORD")) {
            @Override
            public void onFinishLoading(JSONArray result) {
                List<File> fileList = new ArrayList<>();
                File file = null;
                for (int i = 0; i < result.length(); i++) {
                    try {
                        file = new Gson().fromJson(result.get(i).toString(), File.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fileList.add(file);
                }
                AppStorage.adapterRemote = new ListViewAdapterDisk(MainPageActivity.this, fileList);
                ((ListView) findViewById(R.id.files)).setAdapter(AppStorage.adapterRemote);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page, menu);
        findViewById(R.id.logout).setOnClickListener(e-> {
            Configuration.getConfiguration(getApplicationContext()).addOrModifyConfiguration("autologin", "false");
            startActivity(new Intent(this, LoginActivity.class));
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_page);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}