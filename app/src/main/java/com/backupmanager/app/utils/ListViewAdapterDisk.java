package com.backupmanager.app.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.backupmanager.api.BackupAPI;
import com.backupmanager.app.R;
import com.backupmanager.app.ui.DetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterDisk extends ArrayAdapter<File> {

    private Context context;
    private List<File> fileList;

    public ListViewAdapterDisk(@NonNull Context context, List<File> arrayList) {
        super(context, 0, arrayList);
        this.context = context;
        this.fileList = new ArrayList<>(arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
        }
        ImageView options = currentItemView.findViewById(R.id.imageView6);
        options.setOnClickListener(e->{
            PopupMenu popupMenu = new PopupMenu(context, options);

            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_disk, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item-> {
                if(item.getTitle().equals("Properties")){
                    Intent properties = new Intent(context.getApplicationContext(), DetailsActivity.class);
                    File file = getItem(position);
                    properties.putExtra("FILE_NAME", checkIfEmpty(file.getName()));
                    properties.putExtra("FILE_SIZE", file.getSize());
                    properties.putExtra("FILE_DIR", file.isDirectory());
                    properties.putExtra("FILE_EXT", file.isDirectory() ? "/" : checkIfEmpty(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
                    properties.putExtra("FILE_LPATH", checkIfEmpty(file.getRemotePath()));
                    properties.putExtra("FILE_HASH", checkIfEmpty(file.getHash()));
                    properties.putExtra("FILE_RPATH", checkIfEmpty(file.getLocalPath()));
                    context.startActivity(properties);
                }
                if(item.getTitle().equals("Download")){
                    BackupAPI.downloadFile(getItem(position).getLocalPath());
                }
                return true;
            });
            popupMenu.show();
        });

        File file = getItem(position);
        ImageView image = currentItemView.findViewById(R.id.imageView);

        if(file.isDirectory()){
            image.setImageResource(R.drawable.folder);
        }else{
            image.setImageResource(R.drawable.file);
        }

        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(file.getName());

        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(file.isDirectory() ? file.getSize() + "" : getFileSize(file.getSize()));

        return currentItemView;
    }

    private String checkIfEmpty(String name) {
        if(name != null && !name.isEmpty()){
            return name;
        }
        return "/";
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
