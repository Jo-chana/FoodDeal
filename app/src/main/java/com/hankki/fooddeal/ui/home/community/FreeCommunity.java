package com.hankki.fooddeal.ui.home.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.ux.recyclerview.SetRecyclerViewOption;
import com.scalified.fab.ActionButton;

public class FreeCommunity extends Fragment {
    RecyclerView recyclerView;
    ActionButton fab;
    View view;
    SetRecyclerViewOption setRecyclerViewOption;
    StaticPost staticPost = new StaticPost();

    public FreeCommunity(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_free, container, false);
        setPostLists();
        setRecyclerView();
        setPostWrite();
        return view;
    }

    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_free);
        fab = view.findViewById(R.id.fab_write);
        setRecyclerViewOption = new SetRecyclerViewOption(
                recyclerView, fab, view, getContext(), R.layout.community_item );
        setRecyclerViewOption.setPostItems(staticPost.getPostList(2));
        setRecyclerViewOption.build(2);
    }

    public void setPostLists(){
        staticPost.FreeDefault();
    }

    public void setPostWrite(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**글 쓰기 버튼 클릭 이벤트*/
                Intent intent = new Intent(getContext(),PostActivity.class);
                intent.putExtra("mode","write");
                intent.putExtra("page",2);
                startActivity(intent);
            }
        });
    }
    public void distanceSorting(){
        setRecyclerViewOption.sortPostItems();
    }
}
