package com.hankki.fooddeal.ui.register;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.RegularCheck;
import com.hankki.fooddeal.data.security.AES256Util;
import com.hankki.fooddeal.data.security.HashMsgUtil;
import com.hankki.fooddeal.ui.IntroActivity;
import com.hankki.fooddeal.ui.register.location.LocationMainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/** 회원가입 회면
 *  소비자용, 사업자용 버튼 이용해서 선택*/

public class RegisterActivity extends AppCompatActivity {

    private final static Integer DATE_REQUEST_CODE = 1001;
    private final static List<String> userIDList = new ArrayList<>(Arrays.asList("dlguwn13", "ggj0418", "tkyk103000"));

    EditText idEditText, passwordEditText, passwordCheckEditText, emailEditText, nameEditText, birthDateEditText;
    ImageView calendarImageView;
    TextView passwordHintTextView, passwordCheckHintTextView, emailHintTextView, idDupHintTextView;
    Button idDupCheckButton, preButton, postButton;
    RadioGroup userTypeRadioGroup;
    RadioButton personTypeRadioButton, sellerTypeRadioButton;

    String phoneNo, userType;

    boolean isBackPressed = false;
    boolean isCalendarClicked = false;

    // 기입률 진행 상황 알림 변수
    boolean isIdVeified = false;
    boolean isRegularPassword = false;
    boolean isPasswordMatch = false;
    boolean isRegularEmail = false;
    boolean isBirthDateDone = false;
    boolean isUserTypeDone = false;

    Timer checkPasswordTimer, checkPasswordMatchTimer, checkEmailTimer, checkAllInputDoneTimer;
    TimerTask checkPasswordTimerTask, checkPasswordMatchTimerTask, checkEmailTimerTask, checkAllInputDoneTimerTask;

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
                    isBirthDateDone = true;
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

    // 모든 기입들이 제대로 이루어졌는가 체크
    private void checkAllInputDone() {
        checkAllInputDoneTimerTask = new TimerTask() {
            @Override
            public void run() {
                postButton.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isIdVeified && isRegularPassword && isPasswordMatch && isRegularEmail && isBirthDateDone && isUserTypeDone)
                            postButton.setEnabled(true);
                        else
                            postButton.setEnabled(false);
                    }
                });
            }
        };
        checkAllInputDoneTimer.schedule(checkAllInputDoneTimerTask, 0, 1000);
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
                if(hasFocus) {
                    if(idDupHintTextView.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "아이디 중복검사를 진행해주세요!", Toast.LENGTH_LONG).show();
                    else checkPasswordRegular();
                }
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
                isCalendarClicked = true;
                Intent toDatePickerIntent = new Intent(RegisterActivity.this, DatePickerActivity.class);
                startActivityForResult(toDatePickerIntent, DATE_REQUEST_CODE);
            }
        });

        passwordHintTextView = (TextView) findViewById(R.id.register_password_hint_textView);
        passwordCheckHintTextView = (TextView) findViewById(R.id.register_password_check_hint_textView);
        emailHintTextView = (TextView) findViewById(R.id.register_email_hint_textView);
        idDupHintTextView = (TextView) findViewById(R.id.register_id_dup_hint_textView);

        idDupCheckButton = (Button) findViewById(R.id.register_dup_check_button);
        idDupCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString();
                if(id.equals("")) Toast.makeText(getApplicationContext(), "아이디를 입력해주세요!", Toast.LENGTH_LONG).show();
                else if(userIDList.contains(id)) {
                    idDupHintTextView.setText(getString(R.string.register_dup_id));
                    idDupHintTextView.setTextColor(Color.RED);
                    isIdVeified = false;
                } else {
                    idDupHintTextView.setText(getString(R.string.register_not_dup_id));
                    idDupHintTextView.setTextColor(Color.BLUE);
                    isIdVeified = true;
                }
            }
        });
        preButton = (Button) findViewById(R.id.register_pre_button);
        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBackPressed = true;
                finish();
            }
        });
        postButton = (Button) findViewById(R.id.register_post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAllTimeTask();
                Intent toLocationMainIntent = new Intent(RegisterActivity.this, LocationMainActivity.class);
                toLocationMainIntent.putExtra("userID", HashMsgUtil.getSHA256(idEditText.getText().toString()));
                toLocationMainIntent.putExtra("userPassword", HashMsgUtil.getSHA256(passwordEditText.getText().toString()));
                toLocationMainIntent.putExtra("userEmail", emailEditText.getText().toString());
                toLocationMainIntent.putExtra("userBirthDate", birthDateEditText.getText().toString());
                toLocationMainIntent.putExtra("userName", nameEditText.getText().toString());
                toLocationMainIntent.putExtra("phoneNo", AES256Util.aesEncode(phoneNo));
                toLocationMainIntent.putExtra("userType", userType);
                startActivity(toLocationMainIntent);
                finish();
            }
        });

        userTypeRadioGroup = (RadioGroup) findViewById(R.id.register_user_type_radioGroup);
        userTypeRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        personTypeRadioButton = (RadioButton) findViewById(R.id.register_person_radioButton);
        sellerTypeRadioButton = (RadioButton) findViewById(R.id.register_seller_radioButton);

        checkPasswordTimer = new Timer();
        checkPasswordMatchTimer = new Timer();
        checkEmailTimer = new Timer();
        checkAllInputDoneTimer = new Timer();
    }

    // 자원 할당 해제
    private void releaseResource() {
        idEditText = null;
        passwordEditText = null;
        passwordCheckEditText = null;
        emailEditText = null;
        nameEditText = null;
        birthDateEditText = null;

        isCalendarClicked = false;
        // 기입률 진행 상황 알림 변수
        isIdVeified = false;
        isRegularPassword = false;
        isPasswordMatch = false;
        isRegularEmail = false;
        isBirthDateDone = false;
        isUserTypeDone = false;

        calendarImageView = null;

        passwordHintTextView = null;
        passwordCheckHintTextView = null;
        emailHintTextView = null;

        idDupCheckButton = null;
        preButton = null;
        postButton = null;

        userTypeRadioGroup = null;
        personTypeRadioButton = null;
        sellerTypeRadioButton = null;

        checkPasswordTimer = null;
        checkPasswordMatchTimer = null;
        checkEmailTimer = null;
        checkAllInputDoneTimer = new Timer();
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
        if(checkAllInputDoneTimerTask != null) {
            checkAllInputDoneTimerTask.cancel();
            checkAllInputDoneTimerTask = null;
        }
    }

    // 사용자 타입 분류 리스너
    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == R.id.register_person_radioButton) userType = "P";
            else userType = "S";
            isUserTypeDone = true;
        }
    };

    // 사용자에게 보여지기 전 자원 할당
    @Override
    protected void onResume() {
        super.onResume();
        if(!isCalendarClicked) {
            initFindViewById();
            checkAllInputDone();
        }
        else isCalendarClicked = false;
    }

    // 사용자가 뒤로가기버튼으로 액티비티를 종료한 경우에서만 자원 할당 해제
    @Override
    protected void onPause() {
        super.onPause();
        if(isBackPressed) {
            releaseResource();
            stopAllTimeTask();
            isBackPressed = false;
        }
    }

    // 뒤로가기 버튼
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackPressed = true;
        finish();
    }
}
