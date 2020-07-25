package com.hankki.fooddeal.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.hankki.fooddeal.data.retrofit.retrofitDTO.BoardListResponse;
import com.hankki.fooddeal.data.security.AES256Util;

import java.util.ArrayList;
import java.util.HashMap;

/**Post Item
 * 게시글, 채팅, 쿠폰 등
 * Recycler View 구성하는 Item Frame*/
public class PostItem implements Comparable<PostItem>, Parcelable {
    /**DB 데이터에 맞춰서 필드 추가*/

    String boardContent;
    String regionFirst;
    String regionSecond;
    String regionThird;
    String insertDate;
    String boardTitle;
    Bitmap userProfile = null;
    String userLatitude;
    String userLongitude;
    String userToken;
    String userHashId;
    ArrayList<Bitmap> images = new ArrayList<>();
    String category = "";
    int likeCount = 0;
    int commentCount = 0;
    int boardSeq;
    int userSeq;
    String delYN;


    protected PostItem(Parcel in) {
        boardContent = in.readString();
        regionFirst = in.readString();
        regionSecond = in.readString();
        regionThird = in.readString();
        insertDate = in.readString();
        boardTitle = in.readString();
        userProfile = in.readParcelable(Bitmap.class.getClassLoader());
        userLatitude = in.readString();
        userLongitude = in.readString();
        images = in.createTypedArrayList(Bitmap.CREATOR);
        category = in.readString();
        likeCount = in.readInt();
        commentCount = in.readInt();
        boardSeq = in.readInt();
        userSeq = in.readInt();
        delYN = in.readString();
        Distance = in.readInt();
        userHashId = in.readString();
    }

    public static final Creator<PostItem> CREATOR = new Creator<PostItem>() {
        @Override
        public PostItem createFromParcel(Parcel in) {
            return new PostItem(in);
        }

        @Override
        public PostItem[] newArray(int size) {
            return new PostItem[size];
        }
    };

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

    public PostItem(){
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public void setUserProfile(Bitmap userProfile) {
        this.userProfile = userProfile;
    }

    public void setDistance(int distance) {
        Distance = distance;
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public String getBoardTitle(){
        return boardTitle;
    }

    public int getDistance() {
        return Distance;
    }

    public String getInsertDate(){ return insertDate; }

    public Bitmap getUserProfile() {
        return userProfile;
    }

    public String getRegionFirst() {
        return regionFirst;
    }

    public String getRegionSecond() {
        return regionSecond;
    }

    public String getRegionThird() {
        return regionThird;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getBoardSeq() {
        return boardSeq;
    }

    public int getUserSeq() {
        return userSeq;
    }

    public String getUserHashId() {
        return userHashId;
    }

    public String getDelYN() {
        return delYN;
    }

    public void addPostImage(Bitmap image){
        images.add(image);
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }

    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public void setImages(ArrayList<Bitmap> images) {
        this.images = images;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setBoardSeq(int boardSeq) {
        this.boardSeq = boardSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public void setDelYN(String delYN) {
        this.delYN = delYN;
    }

    @Override
    public int compareTo(PostItem o) {
        return this.getDistance() - o.getDistance();
    }

    public void onBindBoardApi(BoardListResponse.BoardResponse boardResponse){
        boardSeq = boardResponse.getBoardSeq();
        boardTitle = boardResponse.getBoardTitle();
        boardContent = boardResponse.getBoardContent();
        userLatitude = AES256Util.aesDecode(boardResponse.getUserLat());
        userLongitude = AES256Util.aesDecode(boardResponse.getUserLon());
        userHashId = boardResponse.getUserHashId();
        regionFirst = boardResponse.getRegionFirst();
        regionSecond = boardResponse.getRegionSecond();
        regionThird = boardResponse.getRegionThird();

        switch(boardResponse.getBoardCodeSeq()){
            case "IN01":
                category = "INGREDIENT EXCHANGE";
                break;
            case "IN02":
                category = "INGREDIENT SHARE";
                break;
            case "FR01":
                category = "FREE";
                break;
            case "RE01":
                category = "RECIPE";
                break;
            default:
                category = boardResponse.getBoardCodeSeq();
        }
        insertDate = boardResponse.getInsertDate();
        likeCount = boardResponse.getLikeCount();
        delYN = boardResponse.getDelYn();
        commentCount = boardResponse.getCommentCount();
    }

    public HashMap<String, String> onBindBodyApi(Context context){
        HashMap<String, String> body = new HashMap<>();
        body.put("BOARD_CODE_SORT", this.getCategory());
        body.put("USER_TOKEN", PreferenceManager.getString(context, "userToken"));
        body.put("BOARD_TITLE",this.getBoardTitle());
        body.put("BOARD_CONTENT",this.getBoardContent());
        body.put("INSERT_DATE",this.getInsertDate());
        body.put("USER_LAT",AES256Util.aesEncode(PreferenceManager.getString(context, "Latitude")));
        body.put("USER_LON",AES256Util.aesEncode(PreferenceManager.getString(context,"Longitude")));
        body.put("REGION_1DEPTH_NAME",PreferenceManager.getString(context, "region1Depth"));
        body.put("REGION_2DEPTH_NAME",PreferenceManager.getString(context, "region2Depth"));
        body.put("REGION_3DEPTH_NAME",PreferenceManager.getString(context, "region3Depth"));

        return body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(boardContent);
        dest.writeString(regionFirst);
        dest.writeString(regionSecond);
        dest.writeString(regionThird);
        dest.writeString(insertDate);
        dest.writeString(boardTitle);
        dest.writeParcelable(userProfile, flags);
        dest.writeString(userLatitude);
        dest.writeString(userLongitude);
        dest.writeTypedList(images);
        dest.writeString(category);
        dest.writeInt(likeCount);
        dest.writeInt(commentCount);
        dest.writeInt(boardSeq);
        dest.writeInt(userSeq);
        dest.writeString(delYN);
        dest.writeInt(Distance);
        dest.writeString(userHashId);
    }
}
