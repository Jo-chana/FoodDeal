package com.hankki.fooddeal.ui.address;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;

public class PopupActivity extends Activity {
    private TextView tv_result;
    private TextView tv_tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

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
