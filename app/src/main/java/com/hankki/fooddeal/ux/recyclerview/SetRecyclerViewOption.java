package com.hankki.fooddeal.ux.recyclerview;


import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;

/**게시판 In Activity Recycler View 옵션 설정 (공통)*/
public class SetRecyclerViewOption {
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    ActionButton fab;
    View view;
    Context context;
    int layout;

    public SetRecyclerViewOption(RecyclerView rv, ActionButton ab, View v,
                                 Context ct, int layout){
        recyclerView = rv;
        fab = ab;
        view = v;
        context = ct;
        this.layout = layout;
    }
    public void build(){
        setRecyclerView();
        setFloatingActionButton();
    }

    public void setRecyclerView(){
        postAdapter = new PostAdapter(context,makePostItems(),layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(postAdapter);
    }

    /**임시 게시글 아이템*/
    public ArrayList<PostItem> makePostItems(){
        ArrayList<PostItem> result = new ArrayList<>();
        for(int i=0;i<20;i++){
            PostItem item = new PostItem();
            result.add(i,item);
        }
        return result;
    }

    /**플로팅 액션 버튼 Fade Out & Fade In*/
    public void setFloatingActionButton(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
    }

}
