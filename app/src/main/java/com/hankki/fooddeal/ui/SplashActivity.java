package com.hankki.fooddeal.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.ForcedTerminationService;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.retrofit.APIClient;
import com.hankki.fooddeal.data.retrofit.APIInterface;
import com.hankki.fooddeal.data.retrofit.retrofitDTO.MemberResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.hankki.fooddeal.ui.address.AddressActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**스플래쉬 화면*/
public class SplashActivity extends AppCompatActivity {
    Intent intent;
    /*
    이현준
    자동 로그인 구현
    */
    private FirebaseAuth firebaseAuth;
    private Activity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 강제종료 알림 서비스
        startService(new Intent(this, ForcedTerminationService.class));

        /**스플래쉬 핸들러 바꾸어야 함.
         * 메인으로 넘어가기 전 로그인 유지 상태인지, 아닌지 판단하여
         * 인트로 액티비티 또는 홈 액티비티로 이동*/

        /* 이현준
        로그인 화면에서 로그인 성공 시, 서버에서 JWT 토큰을 세션 유지용도로 내려줌. 
        이것을 SharedPreferences에 저장한 뒤, 어플 종료시 혹은 마이페이지에서 자동 로그인 해제 시 삭제
        자동 로그인 설정 후 어플리케이션 재실행 시, 스플래시에서 SharedPreferences에 토큰이 있는지 판단해서 있으면 바로 홈 액티비티 없으면 인트로 액티비티
        */
        firebaseAuth = FirebaseAuth.getInstance();

        /*
        이현준
        자동 로그인 구현
        */
        if(!PreferenceManager.getString(getApplicationContext(), "userToken").equals("")) {
            String userToken = PreferenceManager.getString(getApplicationContext(), "userToken");
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<MemberResponse> autoLoginCall = apiInterface.autoLogin(userToken);
            autoLoginCall.enqueue(new Callback<MemberResponse>() {
                @Override
                public void onResponse(Call<MemberResponse> call, Response<MemberResponse> response) {
                    MemberResponse memberResponse = response.body();
                    if (memberResponse != null &&
                            memberResponse.getResponseCode() == 500) {
                        signInWithCustomToken(memberResponse.getFirebaseToken(), memberResponse.getUserToken());
                    }
                }

                @Override
                public void onFailure(Call<MemberResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            /** SharedPreference 기본틀, key 값이나 변수타입은 추후 수정*/
            intent = new Intent(SplashActivity.this, IntroActivity.class);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }


    }

    // 토큰을 사용한 인증
    private void signInWithCustomToken(String firebaseToken, String userToken) {
        firebaseAuth.signInWithCustomToken(firebaseToken)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent toMainIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(toMainIntent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
