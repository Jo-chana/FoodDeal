package com.hankki.fooddeal.ux.itemtouchhelper;

public class ChatRoomItem {
    public int position;

    public String title;
    public String community;
    public String information;

    public ChatRoomItem(int position, String title) {
        this.information = "";
        this.position = position;
        this.title = title;
    }

    public ChatRoomItem(String title, int order){
        this.title = title;
        this.information = "";
        switch(order){
            case 0:
                community = "[교환/나눔]";
                break;
            case 1:
                community = "[레시피]";
                break;
            case 2:
                community = "[자유]";
                break;
            default:
                community = "";
                break;
        }
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getCommunity() {
        return community;
    }

    public String getInformation() {
        return information;
    }
}
