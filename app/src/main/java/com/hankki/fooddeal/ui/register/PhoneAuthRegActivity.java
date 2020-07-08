package com.hankki.fooddeal.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hankki.fooddeal.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

// 본격 회원가입 창 이전에 휴대폰 번호를 인증하는 액티비티
public class PhoneAuthRegActivity extends AppCompatActivity {

    Button authNumSendButton, authNumCheckButton;
    EditText userPhoneNumEditText, authNumEditText;
    TextView timerTextView;
    String randomAuthNum;

    boolean isAuthTimerOver = false;
    boolean isHomePressed = false;

    Disposable disposable;

    Timer timer;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth_reg);
    }

    // 초기 UX 자원 할당
    private void initFindViewById() {
        authNumSendButton = (Button) findViewById(R.id.auth_num_send_button);
        // 인증번호 전송 버튼 클릭 시, 기입한 번호로 인증번호를 전송
        // TODO DB 연동 시, 해당 휴대폰 번호 중복여부 확인
        authNumSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = userPhoneNumEditText.getText().toString();
                if(isRegularPhoneNo(phoneNo)) setSMSTask(phoneNo);
                else Toast.makeText(getApplicationContext(), "올바른 휴대폰 번호가 아닙니다\n휴대폰 번호를 확인해주세요", Toast.LENGTH_LONG).show();
            }
        });
        authNumCheckButton = (Button) findViewById(R.id.auth_num_check_button);
        /*
        인증번호 확인 버튼 클릭 시
        1. 제한시간 내에 인증 번호를 올바르게 입력했을 경우 -> RegisterActivity로 이동
        2. 인증 번호를 올바르게 입력했으나 제한시간을 오바한 경우 -> 재인증 요구
        3. 인증 번호가 올바르지 않은 경우 -> 인증번호 불일치 메시지
        */
        authNumCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authNumInput = authNumEditText.getText().toString();
                if(authNumInput.equals(randomAuthNum)) {
                    if(isAuthTimerOver) {
                        Toast.makeText(getApplicationContext(), "인증유효시간이 초과하였습니다.\n인증번호를 재발급받아주세요", Toast.LENGTH_LONG).show();
                    } else {
                        stopTimerTask();
                        Intent toRegisterIntent = new Intent(PhoneAuthRegActivity.this, RegisterActivity.class);
                        // TODO RegisterActivity에 휴대폰번호 인텐트 전달
                        startActivity(toRegisterIntent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                }
            }
        });
        userPhoneNumEditText = (EditText) findViewById(R.id.user_phone_num_edittext);
        authNumEditText = (EditText) findViewById(R.id.auth_num_edittext);
        timerTextView = (TextView) findViewById(R.id.auth_num_check_timer);
    }

    // 자원 해제
    private void releaseResource() {
        authNumSendButton = null;
        authNumCheckButton = null;
        userPhoneNumEditText = null;
        authNumEditText = null;
        timerTextView = null;
        timer = null;
        timerTask = null;
        disposable = null;
    }

    // 인증번호를 포함한 문자메시지 전송
    private void setSMSTask(String phoneNo) {
        //onPreExecute
        //doInBackground
        disposable = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Random rand = new Random();
                StringBuilder sb = new StringBuilder();

                for(int i=0;i<6;i++) sb.append(Integer.toString(rand.nextInt(10)));

                randomAuthNum = sb.toString();
                String smsText = "HANKKi 인증번호" + "\n[" + randomAuthNum + "]";

                PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);

                SmsManager mSmsManager = SmsManager.getDefault();
                mSmsManager.sendTextMessage(phoneNo, null, smsText, sentIntent, null);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return false;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object result) throws Exception {
                        //onPostExecute
                        // 문자메시지를 전송한 직후에 제한시간 TimerTask를 시작
                        disposable.dispose();
                        startTimerTaks();
                    }
                });
    }

    // TimerTask 시작
    private void startTimerTaks() {
        stopTimerTask();
        timer = new Timer();

        timerTask = new TimerTask() {
            int count = 180;

            @Override
            public void run() {
                timerTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        String min = Integer.toString(count/60);
                        String sec;

                        if((count%60) < 10) sec = "0" + Integer.toString(count%60);
                        else sec = Integer.toString(count%60);

                        String time = "0" + min + ":" + sec;
                        timerTextView.setText(time);
                    }
                });
                count--;
                if(count == 0) {
                    String authNumInput = authNumEditText.getText().toString();
                    if(!authNumInput.equals(randomAuthNum)) isAuthTimerOver = true;
                    stopTimerTask();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    // TimerTask 중지
    private void stopTimerTask() {
        if(timerTask != null) {
            timerTextView.setText("");
            timerTask.cancel();
            timerTask = null;
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
        return false;
    }

    // 기기 잠금여부 체크
    private boolean isDeviceLock(){
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if(myKM != null) return myKM.inKeyguardRestrictedInputMode();
        return false;
    }

    // 입력받은 휴대폰번호의 정규식 여부 체크
    private boolean isRegularPhoneNo(String phoneNo) {
        String regExp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";
        return phoneNo.matches(regExp);
    }
}