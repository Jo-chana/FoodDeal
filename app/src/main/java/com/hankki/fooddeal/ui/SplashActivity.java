package com.hankki.fooddeal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.login.LoginActivity;

/**스플래쉬 화면*/
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        /**스플래쉬 핸들러 바꾸어야 함.
         * 메인으로 넘어가기 전 로그인 유지 상태인지, 아닌지 판단하여
         * 인트로 액티비티 또는 홈 액티비티로 이동*/
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
