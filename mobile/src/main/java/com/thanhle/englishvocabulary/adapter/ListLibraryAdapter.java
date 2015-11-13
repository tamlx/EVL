package com.thanhle.englishvocabulary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.DatabaseHandler;
import com.thanhle.englishvocabulary.resource.LibraryResource;

import java.util.ArrayList;

/**
 * Created by LaiXuanTam on 10/15/2015.
 */
public class ListLibraryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private DatabaseHandler databaseHandler;
    private ArrayList<LibraryResource> list;

    public ListLibraryAdapter(Context context, ArrayList<LibraryResource> list) {
        inflater = LayoutInflater.from(context);
        databaseHandler = new DatabaseHandler(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_library, parent, false);
            holder = new ViewHolder();
            holder.tvLibraryName = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.imgPreview = (ImageView) convertView.findViewById(R.id.imgPreview);
            holder.tvInstall = (TextView) convertView.findViewById(R.id.btn_install_library);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (list != null && list.size() > 0) {
            holder.tvLibraryName.setText(list.get(position).getName());

            if (databaseHandler.checkLibraryExists(list.get(position).get_id())) {
                holder.tvInstall.setText("Installed");
                holder.tvInstall.setTextColor((Color.GREEN));
            } else {
                holder.tvInstall.setTextColor((Color.GREEN));
                holder.tvInstall.setText("Install");
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        public ImageView imgPreview;
        public TextView tvLibraryName, tvInstall;
    }

}
