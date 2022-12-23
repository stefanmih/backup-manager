package com.backupmanager.app.ui.localfiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.backupmanager.app.databinding.FragmentLocalFilesBinding;

public class LocalFilesFragment extends Fragment {

    private FragmentLocalFilesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocalFilesViewModel localFilesViewModel =
                new ViewModelProvider(this).get(LocalFilesViewModel.class);

        binding = FragmentLocalFilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        localFilesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}