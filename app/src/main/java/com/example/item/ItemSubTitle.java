package com.example.item;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemSubTitle implements Parcelable {
    private String subTitleId;
    private String subTitleUrl;
    private String subTitleLanguage;


    protected ItemSubTitle(Parcel in) {
        subTitleId = in.readString();
        subTitleUrl = in.readString();
        subTitleLanguage = in.readString();
    }

    public ItemSubTitle() {

    }

    public ItemSubTitle(String subTitleId, String subTitleLanguage, String subTitleUrl) {
        this.subTitleId = subTitleId;
        this.subTitleLanguage = subTitleLanguage;
        this.subTitleUrl = subTitleUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subTitleId);
        dest.writeString(subTitleUrl);
        dest.writeString(subTitleLanguage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemSubTitle> CREATOR = new Creator<ItemSubTitle>() {
        @Override
        public ItemSubTitle createFromParcel(Parcel in) {
            return new ItemSubTitle(in);
        }

        @Override
        public ItemSubTitle[] newArray(int size) {
            return new ItemSubTitle[size];
        }
    };

    public String getSubTitleId() {
        return subTitleId;
    }

    public void setSubTitleId(String subTitleId) {
        this.subTitleId = subTitleId;
    }

    public String getSubTitleUrl() {
        return subTitleUrl;
    }

    public void setSubTitleUrl(String subTitleUrl) {
        this.subTitleUrl = subTitleUrl;
    }

    public String getSubTitleLanguage() {
        return subTitleLanguage;
    }

    public void setSubTitleLanguage(String subTitleLanguage) {
        this.subTitleLanguage = subTitleLanguage;
    }
}
