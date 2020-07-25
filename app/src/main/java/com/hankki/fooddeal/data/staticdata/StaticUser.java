package com.hankki.fooddeal.data.staticdata;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.security.AES256Util;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticUser {
    public static String name = FirebaseAuth.getInstance().getCurrentUser()!=null?
            AES256Util.aesDecode(FirebaseAuth.getInstance().getCurrentUser().getUid()):"게스트";
    public static Bitmap profile = null;
    public static String location = "";

    public static HashMap<Integer, ArrayList<PostItem>> myPosts = new HashMap<>();
    public static HashMap<Integer, ArrayList<PostItem>> likedPosts = new HashMap<>();

    public static boolean debug = false;

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        StaticUser.location = location;
    }

    public static HashMap<Integer, ArrayList<PostItem>> getMyPosts() {
        return myPosts;
    }

    public static HashMap<Integer, ArrayList<PostItem>> getLikedPosts() {
        return likedPosts;
    }

    public static ArrayList<PostItem> getPagedPosts(HashMap<Integer,ArrayList<PostItem>> map, int page){
        if(map.get(page)==null){
            ArrayList<PostItem> items = new ArrayList<>();
            map.put(page, items);
        }
        return map.get(page);
    }

    public static void setName(String n) {
        name = n;
    }

    public static void setProfile(Bitmap p) {
        profile = p;
    }

    public static String getName() {
        return name;
    }

    public static Bitmap getProfile() {
        return profile;
    }
}
