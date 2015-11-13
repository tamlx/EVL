package com.thanhle.englishvocabulary.adapter;

import java.util.ArrayList;

/**
 * Created by thanhle on 9/21/2014.
 */
public class DictionaryAdapterChildResource {
    public String mean;
    public String wordPharseOrField;
    public ArrayList<String> exams;

    public DictionaryAdapterChildResource(String mean) {
        this.mean = mean;
    }
    public DictionaryAdapterChildResource(String mean, String wordPharseOrField) {
        this.mean = mean;
        this.wordPharseOrField = wordPharseOrField;
    }
}
