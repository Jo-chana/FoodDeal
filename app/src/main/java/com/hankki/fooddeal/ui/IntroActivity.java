package com.hankki.fooddeal.ui;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;

/**로그인 유지 상태가 아닐 경우, 로그인 또는 회원가입을 선택하는 화면*/
public class IntroActivity extends AppCompatActivity {

    private Button btn_login, btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }
}
