package com.hankki.fooddeal.data.retrofit;

import com.google.gson.JsonObject;
import com.hankki.fooddeal.data.retrofit.retrofitDTO.MemberResponse;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @Headers("Content-Type: application/json")
    @GET("member/autoLogin")
    Call<MemberResponse> autoLogin(@Query("USER_TOKEN") String userToken);

    @Headers("Content-Type: application/json")
    @GET("member/login")
    Call<MemberResponse> login(@Query("USER_HASH_ID") String userHashId, @Query("USER_HASH_PW") String userHashPw);

    @Headers("Content-Type: application/json")
    @POST("member/register")
    Call<MemberResponse> register(@Body HashMap<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("member/checkPhoneNo")
    Call<MemberResponse> checkPhoneNo(@Body HashMap<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("member/checkDupID")
    Call<MemberResponse> checkDupID(@Body HashMap<String, String> body);

    @Headers({
            "Content-Type: application/json",
            "Authorization: KakaoAK 5584ccb6bce16722991e3e4d5a0b0dbe"
    })
    @GET("v2/local/search/address")
    Call<ResponseBody> getAddress(@Query("query") String address);
}
