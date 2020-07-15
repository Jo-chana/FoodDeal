package com.hankki.fooddeal.ux.recyclerview;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.PostItem;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.Collections;

/**게시판 In Activity Recycler View 옵션 설정 (공통)*/
public class SetRecyclerViewOption {
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    ActionButton fab = null;
    View view;
    Context context;
    Bundle bundle;
    ArrayList<PostItem> postItems;
    CardView cv = null;
    int layout;
    int direction = RecyclerView.VERTICAL;

    public SetRecyclerViewOption(RecyclerView rv, CardView cardView, View v
                                 , Context ct, int layout){
        recyclerView = rv;
        cv = cardView;
        view = v;
        context = ct;
        this.layout = layout;
    }


    public void build(int page){
        setRecyclerView(page);
        if(fab != null)
            setFloatingActionButton();
        if(cv!=null)
            setCardViewAnimation();
    }
    public void setDirectionHorizontal(){
        direction = RecyclerView.HORIZONTAL;
    }


    public void setRecyclerView(int page){
        postAdapter = new PostAdapter(context,makePostItems(),layout);
        postAdapter.setPage(page);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, direction,false));
        recyclerView.setAdapter(postAdapter);
    }

    public void setPostItems(ArrayList<PostItem> items){
        postItems = items;
    }

    public void sortPostItems(){
            Collections.sort(postItems);
            postAdapter.setPostItems(postItems);
            postAdapter.notifyDataSetChanged();
    }

    /**임시 게시글 아이템*/
    public ArrayList<PostItem> makePostItems(){
        if(postItems != null)
            return postItems;

        ArrayList<PostItem> result = new ArrayList<>();
        for(int i=0;i<20;i++){
            PostItem item = new PostItem();

            if(bundle!=null) {
                if (bundle.getParcelableArrayList(String.valueOf(i)) != null) {
                    ArrayList<CommentItem> comments = bundle.getParcelableArrayList(String.valueOf(i));
                    item.setComments(comments);
                }
            }
            item.setUserName("익명 "+i);
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

    public void setCardViewAnimation() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && cv.getVisibility() == View.VISIBLE) {
                    cv.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_out));
                    cv.setVisibility(View.INVISIBLE);
                } else if (dy < 0 && cv.getVisibility() != View.VISIBLE) {
                    cv.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in));
                    cv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}
