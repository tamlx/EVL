package com.thanhle.englishvocabulary.resource;

import android.content.ContentValues;

/**
 * Model của User
 * Created by thanhle on 2/11/2015.
 */
public class UserResource implements BaseResource {
    public String username;
    public String email;
    public String fullname;
    public String token;
    public String provider;
    public int level;
    public LibraryInstalledResource [] libraries;

    @Override
    public ContentValues prepareContentValue() {
        return null;
    }
}
