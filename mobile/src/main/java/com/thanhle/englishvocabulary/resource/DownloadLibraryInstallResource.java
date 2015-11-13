package com.thanhle.englishvocabulary.resource;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LaiXuanTam on 11/6/2015.
 */
public class DownloadLibraryInstallResource implements Parcelable {

    public String libraryId;
    public String libraryName;
    public boolean download_success;
    public int count;

    public DownloadLibraryInstallResource(String libraryName, String libraryId, int count) {
        this.libraryName = libraryName;
        this.libraryId = libraryId;
        this.count = count;
    }

    protected DownloadLibraryInstallResource(Parcel in) {
        libraryId = in.readString();
        libraryName = in.readString();
        count = in.readInt();
    }

    public static final Creator<DownloadLibraryInstallResource> CREATOR = new Creator<DownloadLibraryInstallResource>() {
        @Override
        public DownloadLibraryInstallResource createFromParcel(Parcel in) {
            return new DownloadLibraryInstallResource(in);
        }

        @Override
        public DownloadLibraryInstallResource[] newArray(int size) {
            return new DownloadLibraryInstallResource[size];
        }
    };

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public boolean isDownload_success() {
        return download_success;
    }

    public void setDownload_success(boolean download_success) {
        this.download_success = download_success;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(libraryId);
        dest.writeString(libraryName);
        dest.writeInt(count);
    }
}
