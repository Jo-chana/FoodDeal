package com.hankki.fooddeal.ui.chatting.chatDTO;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatRoomModel {
    private String roomId;
    private String roomTitle;
    private ArrayList<String> roomUserList;
    private Map<String, Integer> unreadMemberCountMap;
    private String lastMessageContent;
    @ServerTimestamp
    private Date lastMessageTime;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String roomId, String roomTitle, ArrayList<String> roomUserList, Map<String, Integer> unreadMemberCountMap, String lastMessageContent, Date lastMessageTime) {
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.roomUserList = roomUserList;
        this.unreadMemberCountMap = unreadMemberCountMap;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageTime = lastMessageTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public ArrayList<String> getRoomUserList() {
        return roomUserList;
    }

    public void setRoomUserList(ArrayList<String> roomUserList) {
        this.roomUserList = roomUserList;
    }

    public Map<String, Integer> getUnreadMemberCountMap() {
        return unreadMemberCountMap;
    }

    public void setUnreadMemberCountMap(Map<String, Integer> unreadMemberCountMap) {
        this.unreadMemberCountMap = unreadMemberCountMap;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
