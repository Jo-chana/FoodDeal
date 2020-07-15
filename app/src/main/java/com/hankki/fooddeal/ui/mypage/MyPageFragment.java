package com.hankki.fooddeal.ui.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;

/**마이페이지 화면*/
public class MyPageFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 로그아웃 혹은 자동 로그인 해제
        Button button = view.findViewById(R.id.deleteShared);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.removeKey(getContext(), "userToken");
                FirebaseAuth.getInstance().signOut();
            }
        });

        return view;
    }
}
