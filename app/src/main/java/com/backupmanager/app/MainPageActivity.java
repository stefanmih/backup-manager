package com.backupmanager.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.backupmanager.api.DirectoriesAPI;
import com.backupmanager.app.utils.File;
import com.backupmanager.app.utils.ListViewAdapter;
import com.backupmanager.data.AppStorage;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

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
        binding.appBarMainPage.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        getFilesFromRepository("");
    }

    @Override
    public void onBackPressed() {
        if (DirectoriesAPI.subPath.equals("%5C")) {
            DirectoriesAPI.subPath = "";
            MainPageActivity.this.finishAndRemoveTask();
            MainPageActivity.this.finishAffinity();
        } else {
            DirectoriesAPI.subPath = DirectoriesAPI.subPath.substring(0, DirectoriesAPI.subPath.length() - 4);
            DirectoriesAPI.subPath = DirectoriesAPI.subPath.substring(0, DirectoriesAPI.subPath.lastIndexOf("%5C"));
            getFilesFromRepository("");
        }
        System.out.println(DirectoriesAPI.subPath);
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
                AppStorage.adapter = new ListViewAdapter(MainPageActivity.this, fileList);
                ((ListView) findViewById(R.id.files)).setAdapter(AppStorage.adapter);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page, menu);
        ((ListView) findViewById(R.id.files)).setOnItemClickListener((adapterView, view, i, l) -> {
            File selectedFile = (File) adapterView.getItemAtPosition(i);
            if (selectedFile.isDirectory())
                getFilesFromRepository(selectedFile.getName());
        });
        ((SearchView)findViewById(R.id.search)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                AppStorage.adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                AppStorage.adapter.getFilter().filter(s);
                return false;
            }
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