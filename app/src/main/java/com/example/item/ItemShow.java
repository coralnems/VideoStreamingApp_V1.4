package com.example.item;

public class ItemShow {

    private String showId;
    private String showName;
    private String showImage;
    private String showDescription;
    private String showLanguage;
    private String showRating;
    private String showContentRating;
    private boolean isPremium = false;

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowImage() {
        return showImage;
    }

    public void setShowImage(String showImage) {
        this.showImage = showImage;
    }

    public String getShowDescription() {
        return showDescription;
    }

    public void setShowDescription(String showDescription) {
        this.showDescription = showDescription;
    }

    public String getShowLanguage() {
        return showLanguage;
    }

    public void setShowLanguage(String showLanguage) {
        this.showLanguage = showLanguage;
    }

    public String getShowRating() {
        return showRating;
    }

    public void setShowRating(String showRating) {
        this.showRating = showRating;
    }

    public String getShowContentRating() {
        return showContentRating;
    }

    public void setShowContentRating(String showContentRating) {
        this.showContentRating = showContentRating;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
