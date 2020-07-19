package com.hankki.fooddeal.data.staticdata;

import android.graphics.Bitmap;

public class StaticUser {
    static String name = "게스트";
    static Bitmap profile = null;

    public StaticUser(String name, Bitmap profile){
        setName(name);
        setProfile(profile);
    }

    public StaticUser(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public Bitmap getProfile() {
        return profile;
    }
}
