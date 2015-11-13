package com.thanhle.englishvocabulary.database.tables;

import android.os.Parcel;
import android.os.Parcelable;

public class LibraryTable implements Parcelable {
	public String code;
	public String name;

    public static final String TABLE_NAME = "libraries";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_NAME = "name";

	public LibraryTable(String code, String name) {
		this.code = code;
		this.name = name;
	}

    public LibraryTable(Parcel in) {
        super();
        code = in.readString();
        name = in.readString();
    }


    public static final Parcelable.Creator<LibraryTable> CREATOR = new Creator<LibraryTable>() {

        @Override
        public LibraryTable[] newArray(int size) {
            return new LibraryTable[size];
        }

        @Override
        public LibraryTable createFromParcel(Parcel source) {
            return new LibraryTable(source);
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
    }
}
