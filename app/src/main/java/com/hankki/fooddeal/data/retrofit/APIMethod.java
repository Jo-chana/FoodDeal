package com.hankki.fooddeal.data.retrofit;

import com.hankki.fooddeal.data.retrofit.retrofitDTO.MemberResponse;

import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIMethod {
    private static APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

    public static Integer login(String userHashID, String userHashPw) {
        final int[] responseCode = new int[1];
        Call<MemberResponse> loginCall = apiInterface.login(userHashID, userHashPw);
        //onPreExecute

        //doInBackground
        Disposable disposable = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                loginCall.enqueue(new Callback<MemberResponse>() {
                    @Override
                    public void onResponse(Call<MemberResponse> call, Response<MemberResponse> response) {
                        try {
                            MemberResponse memberResponse = loginCall.execute().body();
                            responseCode[0] = memberResponse.getResponseCode();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<MemberResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

                return false;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object result) throws Exception {
                        //onPostExecute
                    }
                });

        return responseCode[0];
    }
}
