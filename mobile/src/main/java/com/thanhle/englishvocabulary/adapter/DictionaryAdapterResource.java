package com.thanhle.englishvocabulary.adapter;

import java.util.ArrayList;

/**
 * Created by thanhle on 9/21/2014.
 */
public class DictionaryAdapterResource {
    public static final int TYPE_WORD=0;
    public static final int TYPE_WORD_PHARSE=1;
    public static final int TYPE_WORD_SPECIFIC=2;

    public int dictionaryType;
    public String type;
    public String phonetically;
    public String word;
    public ArrayList<DictionaryAdapterChildResource> items;

    public DictionaryAdapterResource(String word, String phonetically, int dictionaryType, String type) {
        this.dictionaryType = dictionaryType;
        this.type = type;
        this.word = word;
        this.phonetically = phonetically;
    }
}
