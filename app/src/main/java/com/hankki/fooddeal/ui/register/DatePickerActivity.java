package com.hankki.fooddeal.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.DatePicker;

import com.hankki.fooddeal.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

// 날짜 선택 팝업 액티비티
public class DatePickerActivity extends Activity {

    private int mYear = 0, mMonth = 0, mDay = 0;
    private Intent toRegisterIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_date_picker);

        Calendar calendar = new GregorianCalendar();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        datePicker.init(mYear, mMonth, mDay, onDateChangedListener);

        Button button = (Button) findViewById(R.id.date_picker_ok_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캘린더 최종값 전달
                toRegisterIntent = new Intent();
                toRegisterIntent.putExtra("mYear", mYear);
                toRegisterIntent.putExtra("mMonth", mMonth+1);
                toRegisterIntent.putExtra("mDay", mDay);
                setResult(RESULT_OK, toRegisterIntent);
                finish();
            }
        });
    }

    // 캘린더 슬라이드 시, 값을 받는 리스너
    DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
        }
    };

    //바깥레이어 클릭시 안닫히게
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    //안드로이드 백버튼 막기
    @Override
    public void onBackPressed() {
        return;
    }
}