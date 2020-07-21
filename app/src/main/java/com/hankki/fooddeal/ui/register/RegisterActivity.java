package com.hankki.fooddeal.ui.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.RegularCheck;
import com.hankki.fooddeal.data.retrofit.APIClient;
import com.hankki.fooddeal.data.retrofit.APIInterface;
import com.hankki.fooddeal.data.retrofit.retrofitDTO.MemberResponse;
import com.hankki.fooddeal.data.security.AES256Util;
import com.hankki.fooddeal.data.security.HashMsgUtil;
import com.hankki.fooddeal.ui.IntroActivity;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** 회원가입 회면
 *  소비자용, 사업자용 버튼 이용해서 선택*/
// TODO 패스워드 입력 텍스트를 rightDrawable 대신 Toggle로 해야지 InputMethodManager 자원 해제 가능
public class RegisterActivity extends AppCompatActivity {

    private APIInterface apiInterface;

    String phoneNo, userID, userPassword, userEmail;

    Animation animAppearHint;

    View toolbarView;

    TextView idHintTextView, passwordHintTextView, emailHintTextView, toolbarTextView;
    EditText idEditText, passwordEditText, emailEditText;
    ImageView backButton;
    Button dupIDCheckButton, postButton;

    TextWatcher passwordTextWatcher, emailTextWatcher;

    boolean isFirstExecuted = true, isBackPressed, isPasswordVisible, isNewID, isRegularPassword, isRegularEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 암호화 된 전화번호가 넘어왔음
        if(getIntent() != null) phoneNo = getIntent().getStringExtra("phoneNo");
    }

    // 자원할당 및 이벤트 설정
    @SuppressLint("ClickableViewAccessibility")
    private void initFindViewById() {
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // 패스워드의 정규성을 실시간으로 파악
        passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String inputPassword = passwordEditText.getText().toString();
                if(RegularCheck.isRegularPassword(inputPassword)) {
                    passwordHintTextView.setTextColor(Color.BLUE);
                    passwordHintTextView.setText("올바른 비밀번호 형식입니다");
                    isRegularPassword = true;
                    userPassword = HashMsgUtil.getSHA256(inputPassword);
                }
                else {
                    passwordHintTextView.setTextColor(Color.RED);
                    passwordHintTextView.setText("올바르지 않은 비밀번호 형식입니다");
                    isRegularPassword = false;
                }
            }
        };
        // 이메일의 정규성을 실시간으로 파악
        emailTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String inputEmail = emailEditText.getText().toString();
                if(RegularCheck.isRegularEmail(inputEmail)) {
                    emailHintTextView.setTextColor(Color.BLUE);
                    emailHintTextView.setText("올바른 이메일 형식입니다");
                    isRegularEmail = true;
                    userEmail = inputEmail;
                }
                else {
                    emailHintTextView.setTextColor(Color.RED);
                    emailHintTextView.setText("올바르지 않은 이메일 형식입니다");
                    isRegularEmail = false;
                }
            }
        };

        toolbarView = findViewById(R.id.login_toolbar);

        toolbarTextView = toolbarView.findViewById(R.id.toolbar_title);
        toolbarTextView.setText(getString(R.string.activity_register_toolbar));

        idHintTextView = findViewById(R.id.register_id_dup_hint_textView);
        passwordHintTextView = findViewById(R.id.register_password_hint_textView);
        emailHintTextView = findViewById(R.id.register_email_hint_textView);

        // 힌트 텍스트 애니메이션
        animAppearHint = AnimationUtils.loadAnimation(this, R.anim.anim_appear_hint_text);
        animAppearHint.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator));

        // 툴바 이미지 클릭 이벤트
        backButton = toolbarView.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });

        dupIDCheckButton = findViewById(R.id.register_dup_check_button);
        dupIDCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputID = idEditText.getText().toString();
                checkDupID(inputID);
            }
        });

        postButton = findViewById(R.id.register_post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNewID && isRegularEmail && isRegularPassword) { register(phoneNo, userID, userPassword, userEmail); }
                else { Toast.makeText(getApplicationContext(), "입력값을 다시 한번 확인해주세요", Toast.LENGTH_SHORT).show(); }
            }
        });

        idEditText = findViewById(R.id.register_id_editText);
        idEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    idHintTextView.setTextColor(getResources().getColor(R.color.defaultText));
                    idHintTextView.setText(getString(R.string.activity_register_id_hint));
                    idHintTextView.startAnimation(animAppearHint);
                } else { idHintTextView.setText(""); }
            }
        });

        passwordEditText = findViewById(R.id.register_password_editText);
        passwordEditText.addTextChangedListener(passwordTextWatcher);
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[RIGHT].getBounds().width())) {
                        int selection = passwordEditText.getSelectionEnd();
                        if (isPasswordVisible) {
                            // set drawable image
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_eye_close, 0);
                            // hide Password
                            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPasswordVisible = false;
                        } else  {
                            // set drawable image
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_eye_open, 0);
                            // show Password
                            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPasswordVisible = true;
                        }
                        passwordEditText.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    passwordHintTextView.setTextColor(getResources().getColor(R.color.defaultText));
                    passwordHintTextView.setText(getString(R.string.activity_register_password_hint));
                    passwordHintTextView.startAnimation(animAppearHint);
                } else { passwordHintTextView.setText(""); }
            }
        });

        emailEditText = findViewById(R.id.register_email_editText);
        emailEditText.addTextChangedListener(emailTextWatcher);
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    emailHintTextView.setTextColor(getColor(R.color.defaultText));
                    emailHintTextView.setText(getString(R.string.activity_register_email_hint));
                    emailHintTextView.startAnimation(animAppearHint);
                } else { emailHintTextView.setText(""); }
            }
        });
    }

    // 자원할당 해제
    @SuppressLint("ClickableViewAccessibility")
    private void releaseResource() {
        apiInterface = null;

        animAppearHint = null;

        toolbarView = null;

        idHintTextView = null;
        passwordHintTextView = null;
        emailHintTextView = null;

        idEditText = null;
        emailEditText = null;
        passwordEditText = null;

        dupIDCheckButton = null;
        postButton = null;

        emailTextWatcher = null;
        passwordTextWatcher = null;

        backButton = null;
    }

    @SuppressWarnings("NullableProblems")
    private void register(String phone, String id, String password, String email) {
        HashMap<String, String> body = new HashMap<>();
        body.put("USER_PHONE", phone);
        body.put("USER_HASH_ID", id);
        body.put("USER_HASH_PW", password);
        body.put("USER_EMAIL", email);

        Call<MemberResponse> registerCall = apiInterface.register(body);
        registerCall.enqueue(new Callback<MemberResponse>() {
            @Override
            public void onResponse(Call<MemberResponse> call, Response<MemberResponse> response) {
                MemberResponse memberResponse = response.body();
                if (memberResponse != null &&
                        memberResponse.getResponseCode() == 600) {
                    setUserProfiles(id);

                    body.clear();
                } else { Toast.makeText(getApplicationContext(), "서버와의 연결이 불안정합니다", Toast.LENGTH_SHORT).show(); }
            }

            @Override
            public void onFailure(Call<MemberResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @SuppressWarnings("NullableProblems")
    private void checkDupID(String userId) {
        HashMap<String, String> body = new HashMap<>();
        body.put("USER_HASH_ID", AES256Util.aesEncode(userId));

        Call<MemberResponse> checkDupIDCall = apiInterface.checkDupID(body);
        checkDupIDCall.enqueue(new Callback<MemberResponse>() {
            @Override
            public void onResponse(Call<MemberResponse> call, Response<MemberResponse> response) {
                MemberResponse memberResponse = response.body();
                if (memberResponse != null &&
                        memberResponse.getResponseCode() == 605) {
                    idHintTextView.setTextColor(Color.BLUE);
                    idHintTextView.setText("사용가능한 아이디입니다");
                    isNewID = true;
                    userID = AES256Util.aesEncode(userId);

                    body.clear();
                } else {
                    idHintTextView.setTextColor(Color.RED);
                    idHintTextView.setText("이미 존재하는 아이디입니다");
                }
            }

            @Override
            public void onFailure(Call<MemberResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setUserProfiles(String uid) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        final HashMap<String, Object> userProfileMap = new HashMap<>();
        userProfileMap.put("userNickname", "");
        userProfileMap.put("userPhotoUri", "");

        firebaseFirestore
                .collection("users")
                .document(uid)
                .set(userProfileMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent toMainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(toMainIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // 자원 할당
    @Override
    protected void onStart() {
        super.onStart();
        if(isFirstExecuted) {
            initFindViewById();
            isFirstExecuted = false;
        }
    }

    // 자원 할당 해제
    @Override
    protected void onStop() {
        super.onStop();
        if(isBackPressed) {
            releaseResource();
            isBackPressed = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackPressed = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseResource();
        Debug.stopMethodTracing();
    }
}
