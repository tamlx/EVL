package com.thanhle.englishvocabulary.database.tables;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.thanhle.englishvocabulary.adapter.DictionaryAdapterChildResource;
import com.thanhle.englishvocabulary.adapter.DictionaryAdapterResource;

import java.util.ArrayList;

/**
 * Created by thanhle on 9/21/2014.
 */
public class DictionaryTable implements Parcelable {
    public String word;
    public String mean;
    public String pastParticiple;


    public static final String TABLE_NAME = "dictionary";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_MEAN = "mean";

    public DictionaryTable(Parcel in) {
        super();
        word = in.readString();
        mean = in.readString();
    }

    public DictionaryTable(String word, String mean) {
        this.word = word;
        this.mean = mean;
    }

    public DictionaryTable(String text) {
        String[] textSplit = text.split("\t");
        if (textSplit.length == 2) {
            this.word = textSplit[0];
            this.mean = textSplit[1];
        }
    }

    public DictionaryTable(Cursor c) {
        if (c != null) {
            this.word = c.getString(0);
            this.mean = c.getString(1);
        }
    }

    public static final Parcelable.Creator<DictionaryTable> CREATOR = new Creator<DictionaryTable>() {

        @Override
        public DictionaryTable[] newArray(int size) {
            return new DictionaryTable[size];
        }

        @Override
        public DictionaryTable createFromParcel(Parcel source) {
            return new DictionaryTable(source);
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(word);
        dest.writeString(mean);
    }

    private String[] splitMean(String data) {
        ArrayList<String> r = new ArrayList<String>();
        int start = 0;
        int end;
        do {
            end = data.indexOf("\\n", start);
            if (end == -1) {
                r.add(data.substring(start, data.length()));
            } else {
                r.add(data.substring(start, end));
                start = end + 2;
            }
        } while (end >= 0);
        return r.toArray(new String[r.size()]);
    }

    public String getPhonetically() {
        // get first mean
        int end = mean.indexOf("\\n");
        String r = mean.substring(0, end);
        // check having phonetically
        if (r.contains("/")) {
            // get phonetically
            int pos = r.indexOf('/');
            return r.substring(pos);
        } else {
            return "";
        }
    }

    public ArrayList<DictionaryAdapterResource> convertToDictionaryAdapterResource() {
        ArrayList<DictionaryAdapterResource> result = new ArrayList<DictionaryAdapterResource>();
        ArrayList<String> wordPharses = null;
        ArrayList<String> wordSpecifics = null;
        String[] data = splitMean(mean);
        String field, type, mean, exam, wordPharse;
        String phonetically = getPhonetically();
        boolean haveWordPharse = false, hasField = false;
        field = type = mean = exam = wordPharse = "";
        for (int i = 0; i < data.length; i++) {
            // check having domain
            if (data[i].startsWith("@")) {
                if (!data[i].startsWith("@" + word)) {
                    hasField = true;
                    field = data[i].substring(1);
                }
            } else if (data[i].startsWith("*")) {
                // check having type
                type = data[i].substring(1).trim();
                // check having past participle
                if (type.indexOf('(') >= 0 && type.indexOf(')') >= 0) {
                    pastParticiple = type.substring(type.indexOf('(') + 1, type.indexOf(')'));
                    type = type.substring(0, type.indexOf('(')).trim();
                }
                type = type.split(",")[0];
                DictionaryAdapterResource r = new DictionaryAdapterResource(word, phonetically, DictionaryAdapterResource.TYPE_WORD, type);
                result.add(r);
            } else if (data[i].startsWith("-") && !haveWordPharse && !hasField) {
                // check having mean
                mean = data[i].substring(1).trim();
                // don't have word type
                if (result.size() == 0) {
                    DictionaryAdapterResource r = new DictionaryAdapterResource(word, phonetically, DictionaryAdapterResource.TYPE_WORD, "");
                    result.add(r);
                }
                // get last DictionaryAdapterResource
                DictionaryAdapterResource r = result.get(result.size() - 1);
                if (r.items == null) {
                    r.items = new ArrayList<DictionaryAdapterChildResource>();
                }
                r.items.add(new DictionaryAdapterChildResource(mean));
            } else if (data[i].startsWith("=") && result.size() > 0) {
                // check having exam
                exam = data[i].substring(1).trim();
                DictionaryAdapterResource r = result.get(result.size() - 1);
                if (r.items != null && r.items.size() > 0) {
                    DictionaryAdapterChildResource dm = r.items.get(r.items.size() - 1);
                    if (dm.exams == null) {
                        dm.exams = new ArrayList<String>();
                    }
                    dm.exams.add("+ " + exam);
                }
            } else if (data[i].startsWith("!")) {
                haveWordPharse = true;
                wordPharse = data[i].substring(1).trim();
            } else if (data[i].startsWith("-") && haveWordPharse) {
                haveWordPharse = false;
                if (wordPharses == null) {
                    wordPharses = new ArrayList<String>();
                }
                mean = data[i].substring(1).trim();
                wordPharses.add(wordPharse + '\t' + mean);
            } else if (data[i].startsWith("-") && hasField) {
                if (wordSpecifics == null) {
                    wordSpecifics = new ArrayList<String>();
                }
                mean = data[i].substring(1).trim();
                wordSpecifics.add(field + '\t' + mean);
            }
        }

        // add word pharese and word specific
        if (wordPharses != null) {
            DictionaryAdapterResource r = new DictionaryAdapterResource(word, phonetically, DictionaryAdapterResource.TYPE_WORD_PHARSE, "");
            r.items = new ArrayList<DictionaryAdapterChildResource>();
            for (String word : wordPharses) {
                String[] temp = word.split("\t");
                r.items.add(new DictionaryAdapterChildResource(temp[1], temp[0]));
            }
            result.add(r);
        }
        if (wordSpecifics != null) {
            DictionaryAdapterResource r = new DictionaryAdapterResource(word, phonetically, DictionaryAdapterResource.TYPE_WORD_SPECIFIC, "");
            r.items = new ArrayList<DictionaryAdapterChildResource>();
            for (String word : wordSpecifics) {
                String[] temp = word.split("\t");
                r.items.add(new DictionaryAdapterChildResource(temp[1], temp[0]));
            }
            result.add(r);
        }

        return result;
    }
}
