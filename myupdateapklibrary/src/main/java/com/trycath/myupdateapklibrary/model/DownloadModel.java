package com.trycath.myupdateapklibrary.model;

import android.os.Parcel;
import android.os.Parcelable;


public class DownloadModel implements Parcelable {

    private int progress;
    private long currentFileSize;
    private long totalFileSize;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
    }

    public DownloadModel() {
    }

    protected DownloadModel(Parcel in) {
        this.progress = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
    }

    public static final Parcelable.Creator<DownloadModel> CREATOR = new Parcelable.Creator<DownloadModel>() {
        @Override
        public DownloadModel createFromParcel(Parcel source) {
            return new DownloadModel(source);
        }

        @Override
        public DownloadModel[] newArray(int size) {
            return new DownloadModel[size];
        }
    };
}
