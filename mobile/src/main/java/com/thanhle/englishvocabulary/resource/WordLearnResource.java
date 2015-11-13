package com.thanhle.englishvocabulary.resource;

/**
 * Created by LaiXuanTam on 10/27/2015.
 */
public class WordLearnResource {
    private String libraryName;
    private String wordLearn;
    private String wordForget;
    private String totalWord;

    public String getTotalWord() {
        return totalWord;
    }

    public void setTotalWord(String totalWord) {
        this.totalWord = totalWord;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getWordLearn() {
        return wordLearn;
    }

    public void setWordLearn(String wordLearn) {
        this.wordLearn = wordLearn;
    }

    public String getWordForget() {
        return wordForget;
    }

    public void setWordForget(String wordForget) {
        this.wordForget = wordForget;
    }
}
