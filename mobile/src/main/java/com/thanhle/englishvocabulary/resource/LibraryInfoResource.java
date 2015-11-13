package com.thanhle.englishvocabulary.resource;

import java.io.Serializable;

/**
 * Created by LaiXuanTam on 10/15/2015.
 */
public class LibraryInfoResource implements Serializable {
    private static final long serialVersionUID = 1L;
    public String library;
    public String exam;
    public String mean;
    public String meanEng;
    public String type;
    public String phonetically;
    public String word;

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public String getMeanEng() {
        return meanEng;
    }

    public void setMeanEng(String meanEng) {
        this.meanEng = meanEng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhonetically() {
        return phonetically;
    }

    public void setPhonetically(String phonetically) {
        this.phonetically = phonetically;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
