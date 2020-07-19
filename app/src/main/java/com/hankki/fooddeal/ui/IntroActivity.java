package com.hankki.fooddeal.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.login.LoginActivity;
import com.hankki.fooddeal.ui.register.PhoneAuthActivity;

/**로그인 유지 상태가 아닐 경우, 로그인 또는 회원가입을 선택하는 화면*/
public class IntroActivity extends AppCompatActivity {

    private Button btn_login, btn_register;

    /**디버그 모드 로그인 스킵*/
    boolean debug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(debug){
            Intent debugging = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(debugging);
            return;
        }

        onClickButton();
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