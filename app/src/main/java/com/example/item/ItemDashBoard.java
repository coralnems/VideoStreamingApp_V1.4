package com.example.item;

public class ItemDashBoard {

    private String userName;
    private String userEmail;
    private String userImage;
    private String currentPlan;
    private String expiresOn;
    private String lastInvoiceDate;
    private String lastInvoicePlan;
    private String lastInvoiceAmount;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(String currentPlan) {
        this.currentPlan = currentPlan;
    }

    public String getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(String expiresOn) {
        this.expiresOn = expiresOn;
    }

    public String getLastInvoiceDate() {
        return lastInvoiceDate;
    }

    public void setLastInvoiceDate(String lastInvoiceDate) {
        this.lastInvoiceDate = lastInvoiceDate;
    }

    public String getLastInvoicePlan() {
        return lastInvoicePlan;
    }

    public void setLastInvoicePlan(String lastInvoicePlan) {
        this.lastInvoicePlan = lastInvoicePlan;
    }

    public String getLastInvoiceAmount() {
        return lastInvoiceAmount;
    }

    public void setLastInvoiceAmount(String lastInvoiceAmount) {
        this.lastInvoiceAmount = lastInvoiceAmount;
    }
}
