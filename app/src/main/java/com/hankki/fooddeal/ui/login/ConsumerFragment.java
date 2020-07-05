package com.hankki.fooddeal.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.MainActivity;

public class ConsumerFragment extends Fragment {

    Button btn_login;
    EditText et_id;
    EditText et_pw;
    View view;
    CheckBox cb_autologin;

    public ConsumerFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_login, container, false);

        setLogin();
        return view;
    }

    /** DB 검증 후 로그인 처리 로직*/
    public void setLogin(){
        et_id = view.findViewById(R.id.et_id);
        et_pw = view.findViewById(R.id.et_pw);

        autoLoginCheck();

        // TODO 여기에 아이디와 비밀번호 정보를 DB에 보내는 로직 추가

        btn_login = view.findViewById(R.id.btn_consumer_login); // 개인회원 로그인
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString(); // 아이디 정보
                String password = et_pw.getText().toString(); // 비밀번호 정보
                //테스트
                Toast.makeText(getContext(), "id: "+id+"  pw: "+password, Toast.LENGTH_SHORT).show();

                // TODO 아이디, 비밀번호 오류 시 팝업 출력 로직 추가

                // TODO 잘 입력했을 경우 해당 회원 정보 가져오는 로직 추가*/
                Intent startApp = new Intent(getContext(), MainActivity.class);
                startActivity(startApp);
            }
        });
    }

    public void autoLoginCheck(){
        cb_autologin = view.findViewById(R.id.cb_autologin);

        if(cb_autologin.isChecked()){
            /** 자동 로그인 체크되었을 경우*/
        } else {
            /** 아닐 경우*/
        }
    }
}
