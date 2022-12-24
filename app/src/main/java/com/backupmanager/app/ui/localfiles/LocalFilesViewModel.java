package com.backupmanager.app.ui.localfiles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.backupmanager.app.utils.ListViewAdapter;
import com.backupmanager.data.AppStorage;

public class LocalFilesViewModel extends ViewModel {

    private final MutableLiveData<ListViewAdapter> listLocal;

    public LocalFilesViewModel() {
        listLocal = new MutableLiveData<>();
    }

    public LiveData<ListViewAdapter> getAdapter() {
        listLocal.setValue(AppStorage.adapterLocal);
        return listLocal;
    }
}