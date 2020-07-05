package com.hankki.fooddeal.ui.grouppurchase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hankki.fooddeal.R;

/**공동구매 화면*/
public class GroupPurchaseFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_grouppurchase, container, false);

        return view;
    }
}
