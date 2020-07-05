package com.hankki.fooddeal.ui.home.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hankki.fooddeal.R;

public class RecipeShare extends Fragment {

    public RecipeShare(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        return view;
    }
}
