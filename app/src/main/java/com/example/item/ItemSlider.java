package com.example.item;

public class ItemSlider {
    private String id;
    private String sliderTitle;
    private String sliderImage;
    private String sliderType;
    private boolean isPremium = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSliderTitle() {
        return sliderTitle;
    }

    public void setSliderTitle(String sliderTitle) {
        this.sliderTitle = sliderTitle;
    }

    public String getSliderImage() {
        return sliderImage;
    }

    public void setSliderImage(String sliderImage) {
        this.sliderImage = sliderImage;
    }

    public String getSliderType() {
        return sliderType;
    }

    public void setSliderType(String sliderType) {
        this.sliderType = sliderType;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
