package com.hankki.fooddeal.data.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HashMsgUtil {
    public static String getSHA256(String str){
        String SHA = "";
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte[] byteData = sh.digest();
            StringBuilder sb = new StringBuilder();
            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }

    // TODO 채팅방을 더 식별할 수 있는 구분자가 필요
    public static String getSHARoomID(String insertDate, List<String> userList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(insertDate);

        for(String user : userList) stringBuilder.append(user);

        return getSHA256(stringBuilder.toString());
    }
}
