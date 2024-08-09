package com.example.item;

public class ItemTV {
    private String tvId;
    private String tvName;
    private String tvImage;
    private String tvDescription;
    private String tvType;
    private String tvURL;
    private String tvCategory;
    private boolean isPremium = false;
    private String tvView;
    private String tvShareLink;

    public String getTvId() {
        return tvId;
    }

    public void setTvId(String tvId) {
        this.tvId = tvId;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public String getTvImage() {
        return tvImage;
    }

    public void setTvImage(String tvImage) {
        this.tvImage = tvImage;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }


    public String getTvDescription() {
        return tvDescription;
    }

    public void setTvDescription(String tvDescription) {
        this.tvDescription = tvDescription;
    }

    public String getTvType() {
        return tvType;
    }

    public void setTvType(String tvType) {
        this.tvType = tvType;
    }

    public String getTvURL() {
        return tvURL;
    }

    public void setTvURL(String tvURL) {
        this.tvURL = tvURL;
    }

    public String getTvCategory() {
        return tvCategory;
    }

    public void setTvCategory(String tvCategory) {
        this.tvCategory = tvCategory;
    }

    public String getTvView() {
        return tvView;
    }

    public void setTvView(String tvView) {
        this.tvView = tvView;
    }

    public String getTvShareLink() {
        return tvShareLink;
    }

    public void setTvShareLink(String tvShareLink) {
        this.tvShareLink = tvShareLink;
    }
}
