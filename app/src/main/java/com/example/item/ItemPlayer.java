package com.example.item;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemPlayer implements Parcelable {
    private String defaultUrl;
    private boolean isSubTitle = false;
    private boolean isQuality = false;
    private String quality480;
    private String quality720;
    private String quality1080;
    private ArrayList<ItemSubTitle> subTitles;

    protected ItemPlayer(Parcel in) {
        defaultUrl = in.readString();
        isSubTitle = in.readByte() != 0;
        isQuality = in.readByte() != 0;
        quality480 = in.readString();
        quality720 = in.readString();
        quality1080 = in.readString();
        subTitles = in.createTypedArrayList(ItemSubTitle.CREATOR);
    }

    public ItemPlayer() {

    }

    public static final Creator<ItemPlayer> CREATOR = new Creator<ItemPlayer>() {
        @Override
        public ItemPlayer createFromParcel(Parcel in) {
            return new ItemPlayer(in);
        }

        @Override
        public ItemPlayer[] newArray(int size) {
            return new ItemPlayer[size];
        }
    };

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public boolean isSubTitle() {
        return isSubTitle;
    }

    public void setSubTitle(boolean subTitle) {
        isSubTitle = subTitle;
    }

    public boolean isQuality() {
        return isQuality;
    }

    public void setQuality(boolean quality) {
        isQuality = quality;
    }

    public String getQuality480() {
        return quality480;
    }

    public void setQuality480(String quality480) {
        this.quality480 = quality480;
    }

    public String getQuality720() {
        return quality720;
    }

    public void setQuality720(String quality720) {
        this.quality720 = quality720;
    }

    public String getQuality1080() {
        return quality1080;
    }

    public void setQuality1080(String quality1080) {
        this.quality1080 = quality1080;
    }

    public ArrayList<ItemSubTitle> getSubTitles() {
        return subTitles;
    }

    public void setSubTitles(ArrayList<ItemSubTitle> subTitles) {
        this.subTitles = subTitles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(defaultUrl);
        parcel.writeBoolean(isSubTitle);
        parcel.writeBoolean(isQuality);
        parcel.writeString(quality480);
        parcel.writeString(quality720);
        parcel.writeString(quality1080);
        parcel.writeTypedList(subTitles);
    }
}
