package com.backupmanager.app.ui.home;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.backupmanager.app.MainPageActivity;
import com.backupmanager.app.utils.ListViewAdapter;
import com.backupmanager.data.AppStorage;

import java.util.Arrays;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<ListViewAdapter> listView;

    public HomeViewModel() {
        listView = new MutableLiveData<>();
        listView.setValue(AppStorage.adapter);
    }

    public LiveData<ListViewAdapter> getListView() {
        listView.setValue(AppStorage.adapter);
        return listView;
    }
}