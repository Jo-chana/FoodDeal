package com.hankki.fooddeal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.address.AddressActivity;

/**로그인 유지 상태가 아닐 경우, 로그인 또는 회원가입을 선택하는 화면*/
public class IntroActivity extends AppCompatActivity {

    private Button btn_intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        onClickButton();
    }

    private void onClickButton(){
        btn_intro = findViewById(R.id.btn_intro);
        btn_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(IntroActivity.this, AddressActivity.class);
                startActivity(login);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btn_intro = null;
    }
}