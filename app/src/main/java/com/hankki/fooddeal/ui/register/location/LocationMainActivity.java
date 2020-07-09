package com.hankki.fooddeal.ui.register.location;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.register.RegisterActivity;

/**개인, 사업자 공통 주소등록 액티비티
 * 현위치로 주소설정, 직접 입력 두 가지 선택 가능*/
// TODO 다음 버튼 클릭 시, 사용자 종류(userType)에 따라 다른 화면으로 이동
public class LocationMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationmain);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
