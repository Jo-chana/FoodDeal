package com.hankki.fooddeal.data;

public class PurchaseItem {
    String title;
    String imageUrl;
    int originPrice;
    int hotPrice;
    int joinNum;
    int allNum;

    public PurchaseItem(){}

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getOriginPrice() {
        return originPrice;
    }

    public int getHotPrice() {
        return hotPrice;
    }

    public int getJoinNum() {
        return joinNum;
    }

    public int getAllNum() {
        return allNum;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOriginPrice(int originPrice) {
        this.originPrice = originPrice;
    }

    public void setHotPrice(int hotPrice) {
        this.hotPrice = hotPrice;
    }

    public void setJoinNum(int joinNum) {
        this.joinNum = joinNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

}
