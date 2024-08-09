package com.example.item;

public class ItemPlan {
    private String planId;
    private String planName;
    private String planDuration;
    private String planPrice;
    private String planCurrencyCode;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }

    public String getPlanCurrencyCode() {
        return planCurrencyCode;
    }

    public void setPlanCurrencyCode(String planCurrencyCode) {
        this.planCurrencyCode = planCurrencyCode;
    }
}
