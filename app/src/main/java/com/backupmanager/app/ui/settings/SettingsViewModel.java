package com.backupmanager.app.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<Boolean> getValue() {
        return mText;
    }
}