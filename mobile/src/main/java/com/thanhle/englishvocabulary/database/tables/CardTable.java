package com.thanhle.englishvocabulary.database.tables;

import android.os.Parcel;
import android.os.Parcelable;

public class CardTable implements Parcelable {
	public String word;
	public String phonetically;
	public String type;
	public String meanEng;
	public String mean;
	public String exam;
	public String library;

	public static final String TABLE_NAME = "cards";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_PHONETICALLY = "phonetically";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_MEAN_ENG = "mean_eng";
	public static final String COLUMN_MEAN = "mean";
	public static final String COLUMN_EXAM = "exam";
	public static final String COLUMN_LIBRARY = "library";

	public CardTable(String word, String phonetically, String type,
			String meanEng, String mean, String exam, String library) {
		this.word = word;
		this.phonetically = phonetically;
		this.type = type;
		this.meanEng = meanEng;
		this.mean = mean;
		this.exam = exam;
		this.library = library;
	}

	public String toWearString() {
		return word + '\t' + phonetically + '\t' + type + '\t' + mean;
	}

	/**
	 * Constructor base on array of value, order is {word, phonetically, type,
	 * meanEng, mean, exam}
	 * 
	 * @param values
	 */
	public CardTable(String[] values, String library) {
		if (values.length <= 6) {
			this.word = values[0];
			this.phonetically = values[1];
			this.type = values[2];
			this.meanEng = values[3];
			this.mean = values[4];
			this.library = library;
		}
		if (values.length >= 6) {
			this.exam = values[5];
		}
	}

	public CardTable(Parcel in) {
		super();
		library = in.readString();
		word = in.readString();
		phonetically = in.readString();
		type = in.readString();
		meanEng = in.readString();
		mean = in.readString();
		exam = in.readString();
	}

	public static final Parcelable.Creator<CardTable> CREATOR = new Creator<CardTable>() {

		@Override
		public CardTable[] newArray(int size) {
			// TODO Auto-generated method stub
			return new CardTable[size];
		}

		@Override
		public CardTable createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new CardTable(source);
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
		dest.writeString(phonetically);
		dest.writeString(type);
		dest.writeString(meanEng);
		dest.writeString(mean);
		dest.writeString(exam);
	}
}
