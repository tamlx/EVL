package com.thanhle.englishvocabulary.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private List<DictionaryAdapterResource> list;

    public DictionaryAdapter(Context context, ArrayList<DictionaryAdapterResource> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void clear() {
        list.clear();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        DictionaryAdapterResource place = list.get(groupPosition);
        if (place.items == null) {
            return null;
        } else {
            return place.items.get(childPosition);
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 100 + childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final ChildViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_dictionary_child, parent, false);
            holder = new ChildViewHolder();
            holder.tvMean = (TextView) convertView.findViewById(R.id.tvMean);
            holder.tvExam = (TextView) convertView.findViewById(R.id.tvExam);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        DictionaryAdapterResource group = (DictionaryAdapterResource) getGroup(groupPosition);
        DictionaryAdapterChildResource item = (DictionaryAdapterChildResource) getChild(groupPosition, childPosition);
        switch (group.dictionaryType) {
            case DictionaryAdapterResource.TYPE_WORD:
                holder.tvMean.setVisibility(View.VISIBLE);
                holder.tvMean.setText("- " + item.mean);
                if (item.exams!=null) {
                    String exam = TextUtils.join("\n", item.exams);
                    holder.tvExam.setText(exam);
                    holder.tvExam.setVisibility(View.VISIBLE);
                } else {
                    holder.tvExam.setVisibility(View.GONE);
                }
                break;
            case DictionaryAdapterResource.TYPE_WORD_PHARSE:
                holder.tvMean.setVisibility(View.VISIBLE);
                holder.tvMean.setText("- " + item.wordPharseOrField);
                holder.tvExam.setVisibility(View.VISIBLE);
                holder.tvExam.setText(item.mean);
                break;
            case DictionaryAdapterResource.TYPE_WORD_SPECIFIC:
                if (childPosition==0) {
                    holder.tvMean.setText("- " + item.wordPharseOrField);
                    holder.tvMean.setVisibility(View.VISIBLE);
                } else {
                    DictionaryAdapterChildResource preItem = (DictionaryAdapterChildResource) getChild(groupPosition, childPosition-1);
                    if (item.wordPharseOrField.equals(preItem.wordPharseOrField)) {
                        holder.tvMean.setVisibility(View.GONE);
                    } else {
                        holder.tvMean.setText("- " + item.wordPharseOrField);
                        holder.tvMean.setVisibility(View.VISIBLE);
                    }
                }
                holder.tvExam.setVisibility(View.VISIBLE);
                holder.tvExam.setText("+ " + item.mean);
                break;
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        DictionaryAdapterResource place = list.get(groupPosition);
        if (place.items == null) {
            return 0;
        } else {
            return place.items.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_dictionary_group, parent, false);
            holder = new GroupViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvArrow = (TextView) convertView.findViewById(R.id.tvArrow);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        if (isExpanded) {
            holder.tvArrow.setText("-");
        } else {
            holder.tvArrow.setText("+");
        }

        DictionaryAdapterResource group = (DictionaryAdapterResource) getGroup(groupPosition);
        switch (group.dictionaryType) {
            case DictionaryAdapterResource.TYPE_WORD:
                holder.tvTitle.setText(group.type);
                break;
            case DictionaryAdapterResource.TYPE_WORD_SPECIFIC:
                holder.tvTitle.setText(R.string.word_specific);
                break;
            case DictionaryAdapterResource.TYPE_WORD_PHARSE:
                holder.tvTitle.setText(R.string.word_phrase);
                break;
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    private static class GroupViewHolder {
        public TextView tvTitle;
        public TextView tvArrow;
    }

    private static class ChildViewHolder {
        public TextView tvMean;
        public TextView tvExam;
    }

}
