package com.hankki.fooddeal.data;


import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**Post Item
 * 게시글, 채팅, 쿠폰 등
 * Recycler View 구성하는 Item Frame*/
public class PostItem implements Comparable<PostItem> {
    /**DB 데이터에 맞춰서 필드 추가*/
    String UserName;
    String UserPost;
    String UserLocation;
    String UserTime;
    String UserTitle;
    Uri UserProfile;
    ArrayList<Bitmap> images;

    int Distance; // 내 위치와 글 쓴 사람의 거리 (필터링용)

    ArrayList<CommentItem> comments;

    public PostItem(String username, String userpost, String userloc,
                    String userTitle, String userTime,@Nullable int distance, @Nullable Uri profile,
                    @Nullable ArrayList<Bitmap> images){
        UserName = username;
        UserPost = userpost;
        UserTitle = userTitle;
        UserLocation = userloc;
        UserTime = userTime;
        Distance = distance;
        UserProfile = profile;
        this.images = images;
        comments = new ArrayList<>();
        if(comments==null){
            comments = new ArrayList<>();
        }
    }

    /**테스트용*/
    public PostItem(){
        UserName = "글쓴이";
        UserTitle = "아무말 하자";
        UserPost = "아무말\n\n아무말 대잔치";
        UserLocation = "광진구 화양동";
        UserTime = "";
        UserProfile = null;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserPost(String userPost) {
        UserPost = userPost;
    }

    public void setUserLocation(String userLocation) {
        UserLocation = userLocation;
    }

    public void setUserTitle(String userTitle) {
        UserTitle = userTitle;
    }

    public void setUserProfile(Uri userProfile) {
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

    public String getUserPost() {
        return UserPost;
    }

    public String getUserTitle(){
        return UserTitle;
    }

    public String getUserLocation() {
        return UserLocation;
    }

    public int getDistance() {
        return Distance;
    }

    public String getUserTime(){ return UserTime; }

    public Uri getUserProfile() {
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
