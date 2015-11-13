package com.thanhle.englishvocabulary.database.tables;

import android.os.Parcel;
import android.os.Parcelable;


public class WordTable implements Parcelable  {
	public String library;
	public String word;
	public int isNew;
	public int isNewTesting;
	public int forgetCount;

	public WordTable(String library, String word, int isNew, int forgetCount) {
		this.library = library;
		this.word = word;
		this.isNew = isNew;
		this.forgetCount = forgetCount;
	}

	public static final String TABLE_NAME = "words";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_IS_NEW = "is_new";
	public static final String COLUMN_IS_NEW_TESTING = "is_new_testing";
	public static final String COLUMN_FORGET_COUNT = "forget_count";
	public static final String COLUMN_LIBRARY = "library";

	public static WordTable convertCardToNewWord(CardTable card) {
		return new WordTable(card.library, card.word, 1, 1);
	}

    public WordTable(Parcel in) {
        super();
        library = in.readString();
        word = in.readString();
        isNew = in.readInt();
        isNewTesting = in.readInt();
        forgetCount = in.readInt();
    }

    public static final Parcelable.Creator<WordTable> CREATOR = new Creator<WordTable>() {

        @Override
        public WordTable[] newArray(int size) {
            // TODO Auto-generated method stub
            return new WordTable[size];
        }

        @Override
        public WordTable createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new WordTable(source);
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(library);
        dest.writeString(word);
        dest.writeInt(isNew);
        dest.writeInt(isNewTesting);
        dest.writeInt(forgetCount);
    }
}