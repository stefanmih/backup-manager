package com.backupmanager.app.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.backupmanager.data.AppStorage;

import java.util.ArrayList;
import java.util.Objects;

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
        AppStorage.activity.findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        binding.switch1.setChecked(Configuration.getConfiguration(requireContext()).getParameterValue("backup").equals("true"));
        binding.switch3.setChecked(Configuration.getConfiguration(requireContext()).getParameterValue("only_wifi").equals("true"));
        binding.spinner.setEnabled(binding.switch1.isChecked());
        binding.textView13.setEnabled(binding.switch1.isChecked());
        binding.textView14.setEnabled(binding.switch1.isChecked());
        binding.spinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Daily", "Weekly", "Monthly"}));
        binding.spinner.setSelection(Configuration.getConfiguration(requireContext()).getParameterValue("backup_period").equals("Daily") ? 0 : Configuration.getConfiguration(requireContext()).getParameterValue("backup_period").equals("Weekly") ? 1 : 2);
        binding.switch1.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.textView13.setEnabled(compoundButton.isChecked());
            binding.textView14.setEnabled(compoundButton.isChecked());
            binding.spinner.setEnabled(compoundButton.isChecked());
            Configuration.getConfiguration(requireContext()).addOrModifyConfiguration("backup", compoundButton.isChecked() ? "true" : "false");
        });
        binding.switch3.setOnCheckedChangeListener((compoundButton, b) -> Configuration.getConfiguration(requireContext()).addOrModifyConfiguration("only_wifi", compoundButton.isChecked() ? "true" : "false"));
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Configuration.getConfiguration(requireContext()).addOrModifyConfiguration("backup_period", (String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Configuration.getConfiguration(requireContext()).addOrModifyConfiguration("backup_period", "Weekly");
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