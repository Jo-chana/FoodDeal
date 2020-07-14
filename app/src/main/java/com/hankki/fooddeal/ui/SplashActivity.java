package com.hankki.fooddeal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.ForcedTerminationService;
import com.hankki.fooddeal.data.PreferenceManager;

/**스플래쉬 화면*/
public class SplashActivity extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startService(new Intent(this, ForcedTerminationService.class));

        /**스플래쉬 핸들러 바꾸어야 함.
         * 메인으로 넘어가기 전 로그인 유지 상태인지, 아닌지 판단하여
         * 인트로 액티비티 또는 홈 액티비티로 이동*/
        /* 이현준
        로그인 화면에서 로그인 성공 시, 서버에서 JWT 토큰을 세션 유지용도로 내려줌. 
        이것을 SharedPreferences에 저장한 뒤, 어플 종료시 혹은 마이페이지에서 자동 로그인 해제 시 삭제
        자동 로그인 설정 후 어플리케이션 재실행 시, 스플래시에서 SharedPreferences에 토큰이 있는지 판단해서 있으면 바로 홈 액티비티 없으면 인트로 액티비티
        */

        /** SharedPreference 기본틀, key 값이나 변수타입은 추후 수정*/
        intent = new Intent(SplashActivity.this, IntroActivity.class);

        if(PreferenceManager.getBoolean(this,"autologin")){ //자동 로그인
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

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
