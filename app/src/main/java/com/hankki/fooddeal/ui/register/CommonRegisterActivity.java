package com.hankki.fooddeal.ui.register;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;

/**
 * 회원가입 버튼 눌렀을 때 개인/사업자 공통화면
 * 양식은 아이디/비밀번호/비번확인/생년월일/닉네임/이메일 동일
 * 타이틀 텍스트뷰로 개인가입/사업자가입 구분,
 * 서버에 보내는 회원 유형 정보 역시 구분*/

public class CommonRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonregister);
    }
}
