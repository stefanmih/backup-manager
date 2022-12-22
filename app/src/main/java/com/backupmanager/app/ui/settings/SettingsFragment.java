package com.backupmanager.app.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.backupmanager.app.R;
import com.backupmanager.app.databinding.FragmentSettingsBinding;
import com.backupmanager.app.utils.Configuration;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        @SuppressLint("UseSwitchCompatOrMaterialCode") final Switch textView = binding.autoLogin;
        settingsViewModel.getValue().observe(getViewLifecycleOwner(), textView::setChecked);
        binding.autoLogin.setChecked(Configuration.getConfiguration(getContext()).getParameterValue("autologin").equals("true"));
        binding.autoLogin.setOnCheckedChangeListener((compoundButton, b) -> {
            Configuration.getConfiguration(getContext()).addOrModifyConfiguration("autologin", compoundButton.isChecked() ? "true" : "false");
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}