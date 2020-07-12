package com.hankki.fooddeal.ui.register.location;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.register.RegisterActivity;

/**개인, 사업자 공통 주소등록 액티비티
 * 현위치로 주소설정, 직접 입력 두 가지 선택 가능*/
public class LocationMainActivity extends AppCompatActivity {

    Bundle userInfoBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationmain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 사용자가 정보를 바꿔 입력하려고 화면을 이리저리 옮겨다닐 때에 여러번 입력받을 수 있으므로 onStart에서 받아준다
       if(getIntent().getExtras() != null) {
            Intent fromRegisterIntent = getIntent();
            userInfoBundle = fromRegisterIntent.getExtras();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}