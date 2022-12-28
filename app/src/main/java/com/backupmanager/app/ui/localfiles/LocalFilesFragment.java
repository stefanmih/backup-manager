package com.backupmanager.app.ui.localfiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.backupmanager.app.databinding.FragmentLocalFilesBinding;
import com.backupmanager.app.utils.File;
import com.backupmanager.app.utils.ListViewAdapterDisk;
import com.backupmanager.app.utils.ListViewAdapterLocal;
import com.backupmanager.data.AppStorage;
import com.backupmanager.data.LocalFiles;

import java.util.Locale;

public class LocalFilesFragment extends Fragment {

    private FragmentLocalFilesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocalFilesViewModel localFilesViewModel =
                new ViewModelProvider(this).get(LocalFilesViewModel.class);

        binding = FragmentLocalFilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ListView listView = binding.localList;
        AppStorage.adapterLocal = new ListViewAdapterLocal(requireContext(), LocalFiles.getFiles(""));
        localFilesViewModel.getAdapter().observe(getViewLifecycleOwner(), listView::setAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (((File) adapterView.getItemAtPosition(i)).isDirectory()) {
                AppStorage.adapterLocal = new ListViewAdapterLocal(requireContext(), LocalFiles.getFiles(((File) adapterView.getItemAtPosition(i)).getName()));
                listView.setAdapter(AppStorage.adapterLocal);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                Uri photoURI = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", new java.io.File(((File) adapterView.getItemAtPosition(i)).getLocalPath()));
                intent.setDataAndType(photoURI, getTypeFromExtension(((File) adapterView.getItemAtPosition(i)).getLocalPath().toLowerCase(Locale.ROOT)) + "/*");
                intent.putExtra("mimeType", getTypeFromExtension(((File) adapterView.getItemAtPosition(i)).getLocalPath().toLowerCase(Locale.ROOT)) + "/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Open with:"));
            }
        });
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                AppStorage.adapterLocal.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                AppStorage.adapterLocal.getFilter().filter(s);
                return false;
            }
        });
        return root;
    }

    private String getTypeFromExtension(String localPath) {
        if (localPath.contains(".jpg") || localPath.contains(".png") || localPath.contains(".gif") || localPath.contains(".jpeg") || localPath.contains(".bmp")) {
            return "image";
        }
        if (localPath.contains(".mp4") || localPath.contains(".mpeg") || localPath.contains(".avi") || localPath.contains(".mov") || localPath.contains(".wmv")) {
            return "video";
        }
        if (localPath.contains(".mp3") || localPath.contains(".wav")) {
            return "audio";
        }
        return "*";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}