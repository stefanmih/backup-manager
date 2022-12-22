package com.backupmanager.app.ui.disk;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.backupmanager.app.utils.ListViewAdapter;
import com.backupmanager.data.AppStorage;

public class DiskViewModel extends ViewModel {

    private final MutableLiveData<ListViewAdapter> listView;

    public DiskViewModel() {
        listView = new MutableLiveData<>();
        listView.setValue(AppStorage.adapter);
    }

    public LiveData<ListViewAdapter> getListView() {
        listView.setValue(AppStorage.adapter);
        return listView;
    }
}