package com.backupmanager.app.ui.disk;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.data.AppStorage;

public class DiskViewModel extends ViewModel {

    private final MutableLiveData<ListViewAdapterDisk> listView;
    private final MutableLiveData<String> searchView;

    public DiskViewModel() {
        searchView = new MutableLiveData<>();
        listView = new MutableLiveData<>();
    }

    public LiveData<ListViewAdapterDisk> getListView() {
        listView.setValue(AppStorage.adapterRemote);
        return listView;
    }

    public LiveData<String> getSearchView() {
        searchView.setValue("Search Files");
        return searchView;
    }
}