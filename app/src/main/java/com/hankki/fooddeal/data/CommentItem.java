package com.hankki.fooddeal.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class CommentItem implements Parcelable {
    String userName;
    Bitmap userProfile;

    String message;

    String date;
    public CommentItem(String userName, String message,String date, @Nullable Bitmap profile){
        this.userName = userName;
        this.message = message;
        this.date = date;
        this.userProfile = profile;
    }

    protected CommentItem(Parcel in) {
        userName = in.readString();
        message = in.readString();
        date = in.readString();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getDate(){return date;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(message);
        dest.writeString(date);
    }

    public static final Creator<CommentItem> CREATOR = new Creator<CommentItem>() {
        @Override
        public CommentItem createFromParcel(Parcel in) {
            return new CommentItem(in);
        }

        @Override
        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };
}

