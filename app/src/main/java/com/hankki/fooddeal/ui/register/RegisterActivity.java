package com.hankki.fooddeal.ui.register;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;

/** 회원가입 회면
 *  소비자용, 사업자용 버튼 이용해서 선택*/
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
