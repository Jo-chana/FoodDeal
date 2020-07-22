package com.hankki.fooddeal.data;


import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.hankki.fooddeal.data.staticdata.StaticUser;

import java.util.ArrayList;

/**Post Item
 * 게시글, 채팅, 쿠폰 등
 * Recycler View 구성하는 Item Frame*/
public class PostItem implements Comparable<PostItem> {
    /**DB 데이터에 맞춰서 필드 추가*/
    String UserName; // 글쓴이 이름
    String boardContent;
    String UserLocation;
    String regionFirst;
    String regionSecond;
    String regionThird;
    String insertDate;
    String boardTitle;
    Bitmap UserProfile;
    ArrayList<Bitmap> images;
    String category = "";
    int likeCount = 0;
    int boardSeq;
    int userSeq;

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    int Distance; // 내 위치와 글 쓴 사람의 거리 (필터링용)

    ArrayList<CommentItem> comments;

    public PostItem(String username, String userpost, String userloc,
                    String userTitle, String userTime,@Nullable int distance, @Nullable Bitmap profile,
                    @Nullable ArrayList<Bitmap> images, double x, double y){
        UserName = username;
        boardContent = userpost;
        this.boardTitle = boardTitle;
        UserLocation = userloc;
        this.insertDate = insertDate;
        Distance = distance;
        UserProfile = profile;
        this.images = images;
        comments = new ArrayList<>();
        if(comments==null){
            comments = new ArrayList<>();
        }
    }

    public PostItem(String userpost, String boardTitle, String insertDate,
                    @Nullable int distance, @Nullable ArrayList<Bitmap> images){
        UserName = StaticUser.getName();
        boardContent = userpost;
        this.boardTitle = boardTitle;
        UserLocation = StaticUser.getLocation();
        this.insertDate = insertDate;
        Distance = distance;
        UserProfile = StaticUser.getProfile();
        this.images = images;
        comments = new ArrayList<>();
        if(comments==null){
            comments = new ArrayList<>();
        }
    }

    /**테스트용*/
    public PostItem(){
        UserName = "글쓴이";
        boardTitle = "아무말 하자";
        boardContent = "아무말\n\n아무말 대잔치";
        UserLocation = "광진구 화양동";
        insertDate = "";
        UserProfile = null;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public void setUserLocation(String userLocation) {
        UserLocation = userLocation;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public void setUserProfile(Bitmap userProfile) {
        UserProfile = userProfile;
    }

    public void setComments(ArrayList<CommentItem> comments) {
        this.comments = comments;
    }

    public void setDistance(int distance) {
        Distance = distance;
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public String getUserName() {
        return UserName;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public String getBoardTitle(){
        return boardTitle;
    }

    public String getUserLocation() {
        return UserLocation;
    }

    public int getDistance() {
        return Distance;
    }

    public String getInsertDate(){ return insertDate; }

    public Bitmap getUserProfile() {
        return UserProfile;
    }

    public ArrayList<CommentItem> getComments() {
        return comments;
    }

    public void addPostImage(Bitmap image){
        images.add(image);
    }

    public void addComment(CommentItem comment){
        comments.add(comment);
    }

    @Override
    public int compareTo(PostItem o) {
        return this.getDistance() - o.getDistance();
    }

}
