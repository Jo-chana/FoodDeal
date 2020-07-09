package com.hankki.fooddeal.ui.register;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;

/**개인 회원가입 3단계 (마지막) 프로필 등록 액티비티
 * */
public class UserProfileRegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofilereg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent toRegisterIntent = new Intent(UserProfileRegActivity.this, RegisterActivity.class);
        startActivity(toRegisterIntent);
        finish();
    }
}
