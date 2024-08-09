package com.example.item;

import java.util.ArrayList;

public class ItemHome {
    private String homeId;
    private String homeTitle;
    private String homeType;
    private ArrayList<ItemHomeContent> itemHomeContents;

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getHomeTitle() {
        return homeTitle;
    }

    public void setHomeTitle(String homeTitle) {
        this.homeTitle = homeTitle;
    }

    public String getHomeType() {
        return homeType;
    }

    public void setHomeType(String homeType) {
        this.homeType = homeType;
    }

    public ArrayList<ItemHomeContent> getItemHomeContents() {
        return itemHomeContents;
    }

    public void setItemHomeContents(ArrayList<ItemHomeContent> itemHomeContents) {
        this.itemHomeContents = itemHomeContents;
    }
}
