package com.thanhle.englishvocabulary.resource;

import android.content.ContentValues;

/**
 * Created by LaiXuanTam on 10/7/2015.
 */
public class ListLibraryResource implements BaseResource {

    public LibraryResource[] data;
    public String item_total;

    @Override
    public ContentValues prepareContentValue() {
        return null;
    }
}
