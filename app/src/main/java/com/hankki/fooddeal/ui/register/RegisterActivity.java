package com.hankki.fooddeal.ui.register;

import android.app.DatePickerDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.RegularCheck;

import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/** 회원가입 회면
 *  소비자용, 사업자용 버튼 이용해서 선택*/
/*
TODO 달력 클릭해서 날짜 선택되면 나머지 is 변수들 모두 배교해서 true면 다음 버튼 활성화, 다음 버튼 누르면 finish하고 stopALL 소환!
 아이디 중복처리는 여기 액티비티에 정적으로 값 아무거나 해서 처리
 */

public class RegisterActivity extends AppCompatActivity {

    EditText idEditText, passwordEditText, passwordCheckEditText, emailEditText, nameEditText, birthDateEditText;
    ImageView calendarImageView;
    TextView passwordHintTextView, passwordCheckHintTextView, emailHintTextView;
    Button idDupCheckButton, preButton, postButton;

    String phoneNo;
    boolean isHomePressed = false;
    boolean isRegularPassword = false;
    boolean isPasswordMatch = false;
    boolean isRegularEmail = false;

    private final static Integer DATE_REQUEST_CODE = 1001;

    Timer checkPasswordTimer, checkPasswordMatchTimer, checkEmailTimer;
    TimerTask checkPasswordTimerTask, checkPasswordMatchTimerTask, checkEmailTimerTask;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getExtras() != null) {
            if (requestCode == DATE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(data.getIntExtra("mYear", 0));
                    if(data.getIntExtra("mMonth", 0) < 10) {
                        sb.append(0);
                        sb.append(data.getIntExtra("mMonth", 0));
                    } else {
                        sb.append(data.getIntExtra("mMonth", 0));
                    }
                    if(data.getIntExtra("mDay", 0) < 10) {
                        sb.append(0);
                        sb.append(data.getIntExtra("mDay", 0));
                    } else {
                        sb.append(data.getIntExtra("mDay", 0));
                    }
                    birthDateEditText.setText(sb.toString());
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "onActivityResult 값이 null입니다", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        if(intent.hasExtra("phoneNo")) phoneNo = intent.getStringExtra("phoneNo");
    }

    // 패스워드의 안정성을 실시간으로 체크
    private void checkPasswordRegular() {
        checkPasswordTimerTask = new TimerTask() {
            String password = passwordEditText.getText().toString();;
            @Override
            public void run() {
                passwordHintTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(password != null) {
                            if(RegularCheck.isRegularPassword(password)) {
                                passwordHintTextView.setText(getString(R.string.register_correct_password));
                                passwordHintTextView.setTextColor(Color.BLUE);
                                isRegularPassword = true;
                            } else {
                                passwordHintTextView.setText(getString(R.string.register_wrong_password));
                                passwordHintTextView.setTextColor(Color.RED);
                                isRegularPassword = false;
                            }
                        }
                        password = passwordEditText.getText().toString();
                    }
                });
            }
        };
        checkPasswordTimer.schedule(checkPasswordTimerTask, 0, 100);
    }
    // 비밀번호와 체크값이 일치하는지 실시간으로 체크
    private void checkPasswordMatch() {
        checkPasswordMatchTimerTask = new TimerTask() {
            String password = passwordEditText.getText().toString();
            String passwordCheck = passwordCheckEditText.getText().toString();
            @Override
            public void run() {
                passwordCheckHintTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(password != null && passwordCheck != null) {
                            if(password.equals(passwordCheck)) {
                                passwordCheckHintTextView.setText(getString(R.string.register_match_password_check));
                                passwordCheckHintTextView.setTextColor(Color.BLUE);
                                isPasswordMatch = true;
                            } else {
                                passwordCheckHintTextView.setText(getString(R.string.register_not_match_password_check));
                                passwordCheckHintTextView.setTextColor(Color.RED);
                                isPasswordMatch = false;
                            }
                        }
                        passwordCheck = passwordCheckEditText.getText().toString();
                    }
                });
            }
        };
        checkPasswordMatchTimer.schedule(checkPasswordMatchTimerTask, 0, 100);
    }
    // 이메일이 정규표현식인가 실시간으로 체크
    private void checkEmailRegular() {
        checkEmailTimerTask = new TimerTask() {
            String email = emailEditText.getText().toString();
            @Override
            public void run() {
                emailHintTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(email != null) {
                            if(RegularCheck.isRegularEmail(email)) {
                                emailHintTextView.setText(getString(R.string.register_correct_email));
                                emailHintTextView.setTextColor(Color.BLUE);
                                isRegularEmail = true;
                            } else {
                                emailHintTextView.setText(getString(R.string.register_wrong_email));
                                emailHintTextView.setTextColor(Color.RED);
                                isRegularEmail = false;
                            }
                        }
                        email = emailEditText.getText().toString();
                    }
                });
            }
        };
        checkEmailTimer.schedule(checkEmailTimerTask, 0, 100);
    }

    // 자원 할당
    private void initFindViewById() {
        idEditText = (EditText) findViewById(R.id.register_id_editText);
        passwordEditText = (EditText) findViewById(R.id.register_password_editText);
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) checkPasswordRegular();
            }
        });
        passwordCheckEditText = (EditText) findViewById(R.id.register_password_check_editText);
        passwordCheckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) checkPasswordMatch();
            }
        });
        emailEditText = (EditText) findViewById(R.id.register_email_editText);
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) checkEmailRegular();
            }
        });
        nameEditText = (EditText) findViewById(R.id.register_name_editText);
        birthDateEditText = (EditText) findViewById(R.id.register_birth_date_editText);

        calendarImageView = (ImageView) findViewById(R.id.register_calendar_imageView);
        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDatePickerIntent = new Intent(RegisterActivity.this, DatePickerActivity.class);
                startActivityForResult(toDatePickerIntent, DATE_REQUEST_CODE);
            }
        });

        passwordHintTextView = (TextView) findViewById(R.id.register_password_hint_textView);
        passwordCheckHintTextView = (TextView) findViewById(R.id.register_password_check_hint_textView);
        emailHintTextView = (TextView) findViewById(R.id.register_email_hint_textView);

        idDupCheckButton = (Button) findViewById(R.id.register_dup_check_button);
        preButton = (Button) findViewById(R.id.register_pre_button);
        postButton = (Button) findViewById(R.id.register_post_button);

        checkPasswordTimer = new Timer();
        checkPasswordMatchTimer = new Timer();
        checkEmailTimer = new Timer();
    }

    // 자원 할당 해제
    private void releaseResource() {
        idEditText = null;
        passwordEditText = null;
        passwordCheckEditText = null;
        emailEditText = null;
        nameEditText = null;
        birthDateEditText = null;

        calendarImageView = null;

        passwordHintTextView = null;
        passwordCheckHintTextView = null;
        emailHintTextView = null;

        idDupCheckButton = null;
        preButton = null;
        postButton = null;

        checkPasswordTimer = null;
        checkPasswordMatchTimer = null;
        checkEmailTimer = null;
    }

    // 모든 TimerTask 중지
    private void stopAllTimeTask() {
        if(checkPasswordTimerTask != null) {
            checkPasswordTimerTask.cancel();
            checkPasswordTimerTask = null;
        }
        if(checkPasswordMatchTimerTask != null) {
            checkPasswordMatchTimerTask.cancel();
            checkPasswordMatchTimerTask = null;
        }
        if(checkEmailTimerTask != null) {
            checkEmailTimerTask.cancel();
            checkEmailTimerTask = null;
        }
    }

    // 사용자에게 보여지기 전 자원 할당
    @Override
    protected void onResume() {
        super.onResume();
        initFindViewById();
    }

    // 사용자가 뒤로가기버튼으로 액티비티를 종료한 경우에서만 자원 할당 해제
    @Override
    protected void onPause() {
        super.onPause();
        if(!isHomePressed && !isScreenOn() && !isDeviceLock()) {
            releaseResource();
            stopAllTimeTask();
            isHomePressed = false;
        }
    }

    // 홈버튼 푸쉬 이벤트
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        isHomePressed = true;
    }

    // 화면꺼짐 체크
    private boolean isScreenOn(){
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(pm != null) return pm.isInteractive();
        else return false;
    }

    // 기기 잠금여부 체크
    private boolean isDeviceLock(){
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if(myKM != null) return myKM.inKeyguardRestrictedInputMode();
        else return false;
    }
}
