package com.hankki.fooddeal.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
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

    // 자원 할당
    private void initFindViewById() {
        authNumSendButton = (Button) findViewById(R.id.auth_num_send_button);
        authNumSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSMSTask(userPhoneNumEditText.getText().toString());
            }
        });
        authNumCheckButton = (Button) findViewById(R.id.auth_num_check_button);
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
                        disposable.dispose();
                        startTimerTaks();
                    }
                });
    }

    private void startTimerTaks() {
        stopTimerTask();
        timer = new Timer();

        timerTask = new TimerTask() {
            int count = 300;

            @Override
            public void run() {
                timerTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        String min = Integer.toString(count/60);
                        String sec = Integer.toString(count%60);
                        String time = "0" + min + ":" + sec;
                        timerTextView.setText(time);
                    }
                });
                count--;
                if(count == 0) {
                    String authNumInput = authNumEditText.getText().toString();
                    if(!authNumInput.equals(randomAuthNum)) isAuthTimerOver = true;
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void stopTimerTask() {
        if(timerTask != null) {
            timerTextView.setText("");
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFindViewById();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isHomePressed) {
            releaseResource();
            isHomePressed = false;
        }
    }

    // 홈버튼 푸쉬 이벤트
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        isHomePressed = true;
        disposable = null;

    }
}