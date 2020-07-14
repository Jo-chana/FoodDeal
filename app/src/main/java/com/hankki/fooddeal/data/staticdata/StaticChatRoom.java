package com.hankki.fooddeal.data.staticdata;

import com.hankki.fooddeal.ux.itemtouchhelper.ChatRoomItem;

import java.util.ArrayList;

public class StaticChatRoom {
    static ArrayList<ChatRoomItem> chatItems;

    public StaticChatRoom(){
        if(chatItems==null) {
            chatItems = new ArrayList<>();
            chatItems.add(new ChatRoomItem("푸드딜 도우미",-1));
            chatItems.get(0).setInformation("원하는 게시글의 채팅방을 개설해 보세요!");
        }
    }

    public void setChatItems(ArrayList<ChatRoomItem> chatItem){
        chatItems = chatItem;
    }

    public void addChatItem(ChatRoomItem item){
        chatItems.add(item);
    }

    public void deleteChatItem(ChatRoomItem item){
        chatItems.remove(item);
    }

    public ArrayList<ChatRoomItem> getChatItems(){
        return chatItems;
    }

}
