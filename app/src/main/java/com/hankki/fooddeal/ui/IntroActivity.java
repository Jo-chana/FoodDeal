package com.hankki.fooddeal.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.retrofit.APIClient;
import com.hankki.fooddeal.data.retrofit.APIInterface;
import com.hankki.fooddeal.data.retrofit.retrofitDTO.MemberResponse;
import com.hankki.fooddeal.ui.login.LoginActivity;
import com.hankki.fooddeal.ui.register.PhoneAuthActivity;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**로그인 유지 상태가 아닐 경우, 로그인 또는 회원가입을 선택하는 화면*/
public class IntroActivity extends AppCompatActivity {

    private Button btn_login, btn_register;

    /*
       이현준
       자동 로그인 구현
       */
    private FirebaseAuth firebaseAuth;
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        onClickButton();

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




        }
    }

    // 토큰을 사용한 인증
    private void signInWithCustomToken(String firebaseToken, String userToken) {
        firebaseAuth.signInWithCustomToken(firebaseToken)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent toMainIntent = new Intent(IntroActivity.this, MainActivity.class);
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

    private void onClickButton(){
        /**Login*/
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });

        /**Register*/
        /* 이현준
        휴대폰번호 인증 액티비티로 전환
        */
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent register = new Intent(IntroActivity.this, PhoneAuthActivity.class);
                startActivity(register);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btn_login = null;
        btn_register = null;
    }
}