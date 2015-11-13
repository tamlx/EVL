package com.thanhle.englishvocabulary;

import android.os.Parcel;
import android.os.Parcelable;

public class CardTable implements Parcelable {
	public String word;
	public String phonetically;
	public String type;
	public String mean;

	public static final String TABLE_NAME = "cards";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_PHONETICALLY = "phonetically";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_MEAN = "mean";

	public CardTable(String word, String phonetically, String type, String mean) {
		this.word = word;
		this.phonetically = phonetically;
		this.type = type;
		this.mean = mean;
	}


	public CardTable(String cardText) {
		String[] values = cardText.split("\t");
		if (values.length == 4) {
			this.word = values[0];
			this.phonetically = values[1];
			this.type = values[2];
			this.mean = values[3];
		}
	}

	public CardTable(Parcel in) {
		super();
		word = in.readString();
		phonetically = in.readString();
		type = in.readString();
		mean = in.readString();
	}

	public static final Parcelable.Creator<CardTable> CREATOR = new Creator<CardTable>() {

		@Override
		public CardTable[] newArray(int size) {
			return new CardTable[size];
		}

		@Override
		public CardTable createFromParcel(Parcel source) {
			return new CardTable(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(word);
		dest.writeString(phonetically);
		dest.writeString(type);
		dest.writeString(mean);
	}
}
