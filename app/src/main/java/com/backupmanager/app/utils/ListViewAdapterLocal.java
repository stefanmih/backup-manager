package com.backupmanager.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.backupmanager.api.BackupAPI;
import com.backupmanager.app.R;
import com.backupmanager.app.ui.DetailsActivity;
import com.backupmanager.data.AppStorage;
import com.backupmanager.data.LocalFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ListViewAdapterLocal extends ArrayAdapter<File> {

    private Context context;
    private List<File> fileList;

    public ListViewAdapterLocal(@NonNull Context context, List<File> arrayList) {
        super(context, 0, arrayList);
        this.context = context;
        this.fileList = new ArrayList<>(arrayList);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.row_item_local, parent, false);
        }
        LocalFiles.context = context;
        ImageView options = currentItemView.findViewById(R.id.imageView6);
        ((ImageView) currentItemView.findViewById(R.id.imageView5)).setImageResource(android.R.drawable.stat_notify_sync);
        View finalCurrentItemView = currentItemView;
        options.setOnClickListener(e->{
            PopupMenu popupMenu = new PopupMenu(context, options);

            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_local, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item-> {
                if(item.getTitle().equals("Upload")){
                    if(getItem(position).isDirectory()) {
                        List<String> paths = new ArrayList<>();
                        for(java.io.File f : Objects.requireNonNull(new java.io.File(getItem(position).getLocalPath()).listFiles())){
                            paths.add(f.getAbsolutePath());
                        }
                        BackupAPI.requestFilesUpload(paths);
                    }else{
                        BackupAPI.requestFilesUpload(Arrays.asList(getItem(position).getLocalPath()));
                    }
                }
                if(item.getTitle().equals("Properties")){
                    Intent properties = new Intent(context.getApplicationContext(), DetailsActivity.class);
                    File file = getItem(position);
                    properties.putExtra("FILE_NAME", file.getName());
                    properties.putExtra("FILE_SIZE", file.getSize());
                    properties.putExtra("FILE_DIR", file.isDirectory());
                    properties.putExtra("FILE_EXT", file.getExtension());
                    properties.putExtra("FILE_LPATH", file.getLocalPath());
                    properties.putExtra("FILE_HASH", file.getHash());
                    properties.putExtra("FILE_RPATH", file.getRemotePath());
                    context.startActivity(properties);
                }
                if(item.getTitle().equals("Add to backup")){
                    List<String> paths = new ArrayList<>();
                    getFilesToBackup(getItem(position).getLocalPath(), paths);
                    for(String path : paths) {
                        Configuration.getConfiguration(context).addFileForBackup(path);
                    }
                    getItem(position).setBackup(true);
                    (finalCurrentItemView.findViewById(R.id.imageView5)).setVisibility(View.VISIBLE);
                }
                if(item.getTitle().equals("Remove from backup")){
                    List<String> paths = new ArrayList<>();
                    getFilesToBackup(getItem(position).getLocalPath(), paths);
                    getItem(position).setBackup(false);
                    for(String path : paths) {
                        Configuration.getConfiguration(context).removeFileFromBackup(path);
                    }
                    (finalCurrentItemView.findViewById(R.id.imageView5)).setVisibility(View.INVISIBLE);
                }
                return false;
            });
            if(finalCurrentItemView.findViewById(R.id.imageView5).getVisibility() == View.VISIBLE){
                popupMenu.getMenu().getItem(1).setVisible(false);
                popupMenu.getMenu().getItem(2).setVisible(true);
            }else{
                popupMenu.getMenu().getItem(1).setVisible(true);
                popupMenu.getMenu().getItem(2).setVisible(false);
            }
            popupMenu.show();
        });

        File file = getItem(position);
        ImageView image = currentItemView.findViewById(R.id.imageView);

        if(file.isDirectory()){
            image.setImageResource(R.drawable.folder);
        }else{
            image.setImageResource(R.drawable.file);
        }

        if(file.isBackup() || checkIfFolderIsInBackup(file.getLocalPath())){
            (currentItemView.findViewById(R.id.imageView5)).setVisibility(View.VISIBLE);
        }else{
            (currentItemView.findViewById(R.id.imageView5)).setVisibility(View.INVISIBLE);
        }

        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(file.getName());

        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(file.getSize() + "");

        return currentItemView;
    }

    private void getFilesToBackup(String root, final List<String> paths){
        if(new java.io.File(root).isDirectory()){
            for(java.io.File f : Objects.requireNonNull(new java.io.File(root).listFiles())){
                getFilesToBackup(f.getAbsolutePath(), paths);
            }
        }else{
            paths.add(root);
        }
    }

    private boolean checkIfFolderIsInBackup(String path){
        for(String s : Configuration.getConfiguration(context).getFilesForBackup()){
            if(s.contains(path)){
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<File> filteredList = new ArrayList<>();
                for(File file : fileList){
                    if(file.getName().toLowerCase(Locale.ROOT).contains(charSequence.toString().toLowerCase(Locale.ROOT))){
                        filteredList.add(file);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                List<File> files = new ArrayList<>((List<File>)filterResults.values);
                clear();
                addAll(files);
            }
        };
    }
}
