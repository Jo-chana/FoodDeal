package com.hankki.fooddeal.ui.register.location;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;

/**개인, 사업자 공통 주소등록 액티비티
 * 현위치로 주소설정, 직접 입력 두 가지 선택 가능*/
public class LocationMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationmain);
    }
}
