package com.thanhle.englishvocabulary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.resource.WordLearnResource;

import java.util.ArrayList;

/**
 * Created by LaiXuanTam on 10/27/2015.
 */
public class ListWordLearnAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<WordLearnResource> list;
    private Context context;

    public ListWordLearnAdapter(Context context, ArrayList<WordLearnResource> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_update_profile, parent, false);
            holder = new ViewHolder();
            holder.tv_library_name = (TextView) convertView.findViewById(R.id.tv_library_name_update_profile);
            holder.tv_num_word_learn = (TextView) convertView.findViewById(R.id.tv_number_word_learn_update_profile);
            holder.tv_num_word_forget = (TextView) convertView.findViewById(R.id.tv_number_word_forget_update_profile);
            holder.tv_percent_word_lean = (TextView) convertView.findViewById(R.id.tv_number_word_learn_percent_update_profile);
            holder.tvWordforget = (TextView) convertView.findViewById(R.id.tvWordForget);

            holder.pg_word_learn = (ProgressBar) convertView.findViewById(R.id.progressBar_word_learn);
            holder.pg_word_forget = (ProgressBar) convertView.findViewById(R.id.progressBar_word_forget);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list != null && list.size() > 0) {
            holder.tv_library_name.setText(list.get(position).getLibraryName());
            holder.tv_num_word_learn.setText(list.get(position).getWordLearn() + "/" + list.get(position).getTotalWord());
            holder.tv_num_word_forget.setText(list.get(position).getWordForget() + "/" + list.get(position).getWordLearn());
            holder.tvWordforget.setText(context.getString(R.string.update_profile_number_word_forget) + "/" + context.getString(R.string.update_profile_number_word_learn));
            if ((Integer.valueOf(list.get(position).getTotalWord()) > 0)) {
                if (((int) Integer.valueOf(list.get(position).getWordLearn()) * 100 / Integer.valueOf(list.get(position).getTotalWord()) > 0)) {
                    holder.tv_percent_word_lean.setText("" + (int) Integer.valueOf(list.get(position).getWordLearn()) * 100 / Integer.valueOf(list.get(position).getTotalWord()) + "%");
                } else {
                    holder.tv_percent_word_lean.setText("0%");
                }
            } else {
                holder.tv_percent_word_lean.setText("0%");
                holder.pg_word_learn.setSecondaryProgress(99);
            }

            holder.pg_word_learn.setMax(Integer.valueOf(list.get(position).getTotalWord()));
            if (Integer.valueOf(list.get(position).getWordLearn()) > 0) {
                holder.pg_word_learn.setSecondaryProgress(Integer.valueOf(list.get(position).getTotalWord()));
            }

            holder.pg_word_learn.setProgress(Integer.valueOf(list.get(position).getWordLearn()));

            holder.pg_word_forget.setMax(Integer.valueOf(list.get(position).getWordLearn()));
            holder.pg_word_forget.setProgress((int) Integer.valueOf(list.get(position).getWordForget()));
            holder.pg_word_forget.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

//            Drawable drawable = holder.pg_word_learn.getProgressDrawable();
//            drawable.setColorFilter(new LightingColorFilter(0xFF000000, Color.RED));

        }

        return convertView;
    }

    static class ViewHolder {
        public TextView tv_library_name, tv_num_word_learn, tv_num_word_forget, tv_percent_word_lean, tvWordforget;
        public ProgressBar pg_word_learn, pg_word_forget;
    }
}
