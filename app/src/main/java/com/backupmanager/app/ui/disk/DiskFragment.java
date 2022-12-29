package com.backupmanager.app.ui.disk;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.backupmanager.api.DirectoriesAPI;
import com.backupmanager.app.R;
import com.backupmanager.app.databinding.FragmentDiskBinding;
import com.backupmanager.app.utils.Configuration;
import com.backupmanager.app.utils.File;
import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.data.AppStorage;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiskFragment extends Fragment {

    private FragmentDiskBinding binding;
    private ListView listView;
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DiskViewModel diskViewModel =
                new ViewModelProvider(this).get(DiskViewModel.class);

        binding = FragmentDiskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.files;
        searchView = binding.search;
        diskViewModel.getListView().observe(getViewLifecycleOwner(), listView::setAdapter);
        diskViewModel.getSearchView().observe(getViewLifecycleOwner(), searchView::setQueryHint);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            File selectedFile = (File) adapterView.getItemAtPosition(i);
            if (selectedFile.isDirectory()) {
                getFilesFromRepository(selectedFile.getName());
            }
        });
        binding.progressBar3.setVisibility(View.INVISIBLE);
        binding.imageView4.setOnClickListener(view -> AppStorage.adapterRemote.sort((file, t1) -> -file.getName().compareTo(t1.getName())));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                AppStorage.adapterRemote.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                AppStorage.adapterRemote.getFilter().filter(s);
                return false;
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        AppStorage.activity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    private void getFilesFromRepository(String subPath) {
        new DirectoriesAPI(subPath, requireActivity().getIntent().getStringExtra("USERNAME"), requireActivity().getIntent().getStringExtra("PASSWORD")) {
            @Override
            public void onFinishLoading(JSONArray result) {
                List<File> fileList = new ArrayList<>();
                File file = null;
                for (int i = 0; i < result.length(); i++) {
                    try {
                        file = new Gson().fromJson(result.get(i).toString(), File.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fileList.add(file);
                }
                AppStorage.adapterRemote = new ListViewAdapterDisk(requireContext(), fileList);
                listView.setAdapter(AppStorage.adapterRemote);
            }
        }.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}