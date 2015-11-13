package com.thanhle.englishvocabulary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.resource.DownloadLibraryInstallResource;

import java.util.ArrayList;

/**
 * Created by LaiXuanTam on 11/12/2015.
 */
public class ListLibraryLoadInDialogAdapter extends BaseAdapter {

    private ArrayList<DownloadLibraryInstallResource> listLibrary = new ArrayList<DownloadLibraryInstallResource>();
    private LayoutInflater inflater;
    public ListLibraryLoadInDialogAdapter(Context context, ArrayList<DownloadLibraryInstallResource> listLibrary) {
        this.listLibrary = listLibrary;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listLibrary.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_library_load, parent, false);
            holder = new ViewHolder();
            holder.tvLibraryName = (TextView) convertView.findViewById(R.id.tv_library_name_list_library_load);
            holder.pgLoadLibrary = (ProgressBar)convertView.findViewById(R.id.progressBar_load_library);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvLibraryName.setText(listLibrary.get(position).getLibraryName());

        return convertView;
    }

    public static class ViewHolder {
        public TextView tvLibraryName;
        public ProgressBar pgLoadLibrary;
    }
}
