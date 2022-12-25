package com.backupmanager.app.ui;

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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.imageView2.setImageResource(requireActivity().getIntent().getBooleanExtra("FILE_DIR", false) ? R.drawable.folder : R.drawable.file);
        binding.textView21.setText(requireActivity().getIntent().getStringExtra("FILE_NAME"));
        binding.textView20.setText(requireActivity().getIntent().getLongExtra("FILE_SIZE", 0) + "");
        binding.textView19.setText(requireActivity().getIntent().getStringExtra("FILE_EXT"));
        binding.textView18.setText(requireActivity().getIntent().getStringExtra("FILE_LPATH"));
        binding.textView17.setText(requireActivity().getIntent().getStringExtra("FILE_RPATH"));
        binding.textView10.setText(requireActivity().getIntent().getStringExtra("FILE_HASH"));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}