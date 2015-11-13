package com.thanhle.englishvocabulary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.resource.LibraryInfoResource;

/**
 * Created by LaiXuanTam on 10/22/2015.
 */
public class ListCardWordAdapter extends BaseAdapter {

    private LibraryInfoResource[] list;
    private LayoutInflater inflater;

    public ListCardWordAdapter(Context context, LibraryInfoResource[] list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
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
            convertView = inflater.inflate(R.layout.item_list_cards, parent, false);
            holder = new ViewHolder();
            holder.tvWord = (TextView) convertView.findViewById(R.id.tv_card_word_item_list_card);
            holder.tvMean = (TextView) convertView.findViewById(R.id.tv_card_mean_item_list_card);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvWord.setText(list[position].getWord());
        holder.tvMean.setText(list[position].getMean());

        return convertView;
    }


    public static class ViewHolder {
        public TextView tvWord, tvMean;
    }

}
