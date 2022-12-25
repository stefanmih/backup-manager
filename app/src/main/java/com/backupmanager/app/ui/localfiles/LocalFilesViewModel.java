package com.backupmanager.app.ui.localfiles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.app.utils.ListViewAdapterLocal;
import com.backupmanager.data.AppStorage;

public class LocalFilesViewModel extends ViewModel {

    private final MutableLiveData<ListViewAdapterLocal> listLocal;

    public LocalFilesViewModel() {
        listLocal = new MutableLiveData<>();
    }

    public LiveData<ListViewAdapterLocal> getAdapter() {
        listLocal.setValue(AppStorage.adapterLocal);
        return listLocal;
    }
}