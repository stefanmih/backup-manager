package com.backupmanager.app.ui.disk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.backupmanager.app.databinding.FragmentDiskBinding;

public class DiskFragment extends Fragment {

private FragmentDiskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        DiskViewModel diskViewModel =
                new ViewModelProvider(this).get(DiskViewModel.class);

        binding = FragmentDiskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ListView listView = binding.files;
        diskViewModel.getListView().observe(getViewLifecycleOwner(), listView::setAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}