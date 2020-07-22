package com.hankki.fooddeal.ui.mypage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;

public class MySettingActivity extends AppCompatActivity {

    View toolbarView;
    TextView toolbarTextView;
    ImageView iv_logout, iv_bye_bye;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        mContext = this;
        setComponents();
    }

    public void setComponents(){
        toolbarView = findViewById(R.id.top_toolbar);
        toolbarTextView = toolbarView.findViewById(R.id.toolbar_title);
        toolbarTextView.setText("설정");

        iv_logout = findViewById(R.id.iv_logout);
        iv_bye_bye = findViewById(R.id.iv_bye_bye);

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.removeKey(mContext,"userToken");
                        FirebaseAuth.getInstance().signOut();
                        finishAffinity();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        iv_bye_bye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**회원탈퇴*/
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                builder.setMessage("가지마요...우리 좋았잖아");
                builder.setPositiveButton("잘있어", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        PreferenceManager.removeKey(mContext,"userToken");
                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        finishAffinity();
                    }
                }).setNegativeButton("안갈게", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}