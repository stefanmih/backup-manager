package com.backupmanager.app.ui.localfiles;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.app.utils.ListViewAdapterLocal;
import com.backupmanager.data.AppStorage;

public class LocalFilesViewModel extends ViewModel {

    private final MutableLiveData<ListViewAdapterLocal> listLocal;
    private final MutableLiveData<Integer> progressBar;

    public LocalFilesViewModel() {
        listLocal = new MutableLiveData<>();
        progressBar = new MutableLiveData<>();
    }

    public LiveData<ListViewAdapterLocal> getAdapter() {
        listLocal.setValue(AppStorage.adapterLocal);
        return listLocal;
    }

    public LiveData<Integer> getProgressBar() {
        progressBar.setValue(AppStorage.progressBarLocal == null ? View.INVISIBLE : AppStorage.progressBarLocal.getVisibility());
        return progressBar;
    }
}