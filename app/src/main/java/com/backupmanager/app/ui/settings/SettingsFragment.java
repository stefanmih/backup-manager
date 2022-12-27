package com.backupmanager.app.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.backupmanager.app.R;
import com.backupmanager.app.databinding.FragmentSettingsBinding;
import com.backupmanager.app.utils.Configuration;

import java.util.ArrayList;

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
        binding.spinner.setEnabled(binding.switch1.isChecked());
        binding.textView13.setEnabled(binding.switch1.isChecked());
        binding.textView14.setEnabled(binding.switch1.isChecked());
        binding.spinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Daily", "Weekly", "Monthly"}));
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.textView13.setEnabled(compoundButton.isChecked());
                binding.textView14.setEnabled(compoundButton.isChecked());
                binding.spinner.setEnabled(compoundButton.isChecked());
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}