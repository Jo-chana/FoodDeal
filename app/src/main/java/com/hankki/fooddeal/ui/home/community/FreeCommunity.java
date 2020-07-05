package com.hankki.fooddeal.ui.home.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.grouppurchase.GroupPurchaseFragment;
import com.hankki.fooddeal.ux.recyclerview.SetRecyclerViewOption;
import com.scalified.fab.ActionButton;

public class FreeCommunity extends Fragment {
    RecyclerView recyclerView;
    ActionButton fab;
    View view;

    public FreeCommunity(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_free, container, false);
        setRecyclerView();
        return view;
    }

    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_free);
        fab = view.findViewById(R.id.fab_write);
        SetRecyclerViewOption setRecyclerViewOption = new SetRecyclerViewOption(
                recyclerView, fab, view, getContext(), R.layout.community_item );
        setRecyclerViewOption.build();
    }
}
