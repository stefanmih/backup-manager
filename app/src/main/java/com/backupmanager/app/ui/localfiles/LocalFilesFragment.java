package com.backupmanager.app.ui.localfiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.backupmanager.app.databinding.FragmentLocalFilesBinding;
import com.backupmanager.app.utils.File;
import com.backupmanager.app.utils.ListViewAdapter;
import com.backupmanager.data.AppStorage;
import com.backupmanager.data.LocalFiles;

public class LocalFilesFragment extends Fragment {

    private FragmentLocalFilesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocalFilesViewModel localFilesViewModel =
                new ViewModelProvider(this).get(LocalFilesViewModel.class);

        binding = FragmentLocalFilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ListView listView = binding.localList;
        AppStorage.adapterLocal = new ListViewAdapter(requireContext(), LocalFiles.getFiles(""));
        localFilesViewModel.getAdapter().observe(getViewLifecycleOwner(), listView::setAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppStorage.adapterLocal = new ListViewAdapter(requireContext(), LocalFiles.getFiles(((File) adapterView.getItemAtPosition(i)).getName()));
                listView.setAdapter(AppStorage.adapterLocal);
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