package com.backupmanager.app.utils;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListViewAdapterLocal extends ArrayAdapter<File> {

    private Context context;
    private List<File> fileList;

    public ListViewAdapterLocal(@NonNull Context context, List<File> arrayList) {
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
                return false;
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
        textView2.setText(file.getSize() + "");

        return currentItemView;
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
                    if(file.getName().contains(charSequence)){
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
