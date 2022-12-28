package com.backupmanager.app.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.backupmanager.app.R;
import com.backupmanager.app.databinding.FragmentFirstBinding;

import java.util.Objects;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.imageView2.setImageResource(requireActivity().getIntent().getBooleanExtra("FILE_DIR", false) ? R.drawable.folder : R.drawable.file);
        binding.textView21.setText(requireActivity().getIntent().getStringExtra("FILE_NAME"));
        binding.textView20.setText(requireActivity().getIntent().getBooleanExtra("FILE_DIR", false) ? (requireActivity().getIntent().getLongExtra("FILE_SIZE", 0)) + "" : getFileSize(requireActivity().getIntent().getLongExtra("FILE_SIZE", 0)));
        binding.textView19.setText(requireActivity().getIntent().getStringExtra("FILE_EXT"));
        binding.textView18.setText(requireActivity().getIntent().getStringExtra("FILE_LPATH"));
        binding.textView17.setText(requireActivity().getIntent().getStringExtra("FILE_RPATH"));
        binding.textView10.setText(requireActivity().getIntent().getStringExtra("FILE_HASH"));

    }

    private String getFileSize(long size) {
        long fileSizeInKB = size / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;
        long fileSizeInGB = fileSizeInMB / 1024;
        if(fileSizeInGB > 0)
            return fileSizeInGB + " GB";
        else if(fileSizeInMB > 0)
            return fileSizeInMB + " MB";
        else if(fileSizeInKB > 0)
            return fileSizeInKB + " KB";
        else
            return size + " B";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}