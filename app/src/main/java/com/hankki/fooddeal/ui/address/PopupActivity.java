package com.hankki.fooddeal.ui.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.ui.login.LoginActivity;
import com.hankki.fooddeal.ui.register.PhoneAuthActivity;
import com.hankki.fooddeal.ui.register.RegisterActivity;

public class PopupActivity extends Activity {
    private TextView tv_result;
    private TextView tv_tour;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLoginFromPopupIntent = new Intent(PopupActivity.this, LoginActivity.class);
                startActivity(toLoginFromPopupIntent);
                finish();
            }
        });
        registerButton = findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegisterFromPopupIntent = new Intent(PopupActivity.this, PhoneAuthActivity.class);
                startActivity(toRegisterFromPopupIntent);
                finish();
            }
        });


        //데이터 가져오기
        String location = PreferenceManager.getString(this, "Location");
        tv_result = findViewById(R.id.tv_result);
        tv_result.setText("가입하고 " + location);

        tv_tour = findViewById(R.id.tv_tour);
        tv_tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
