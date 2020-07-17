package com.hankki.fooddeal.ui.home.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.ux.recyclerview.SetRecyclerViewOption;


public class ExchangeAndShare extends Fragment {

    View view;
    RecyclerView recyclerView;
    CardView cv_postWrite, cv_showExchange, cv_showShare;
    FrameLayout fl_exchange, fl_share;
    StaticPost staticPost = new StaticPost();
    SetRecyclerViewOption setRecyclerViewOption;
    String category = "식재교환";
    boolean fromMyPage = false;

    public ExchangeAndShare(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_exchange, container, false);
        if(!fromMyPage) {
            setPostLists();
            setShowLists();
            setRecyclerView();
            setPostWrite();
        } else {
            setMyPostOption();
        }
        return view;
    }

    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_exchange);
        cv_postWrite = view.findViewById(R.id.cv_post);
        setRecyclerViewOption = new SetRecyclerViewOption(recyclerView, cv_postWrite,view,getContext(),R.layout.community_item);

        /**메인일 때*/
        setRecyclerViewOption.setPostItems(staticPost.getPostList(0));
        /**내가 쓴 글일 때*/

        /**찜한 글일 때*/

        setRecyclerViewOption.build(0);
    }

    public void setPostLists(){
        staticPost.ExchangeAndShareDefault();
    }

    public void setShowLists(){
        cv_showExchange = view.findViewById(R.id.cv_exchange);
        cv_showShare = view.findViewById(R.id.cv_share);
        fl_exchange = view.findViewById(R.id.fl_exchange);
        fl_share = view.findViewById(R.id.fl_share);
        /**교환 게시글 보이기*/
        cv_showExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "식재교환";
                fl_exchange.setBackgroundResource(R.drawable.cardview_unselector);
                fl_share.setBackgroundResource(R.drawable.cardview_selector);
                /**교환 게시글 필터링*/
            }
        });
        cv_showShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "식재나눔";
                fl_exchange.setBackgroundResource(R.drawable.cardview_selector);
                fl_share.setBackgroundResource(R.drawable.cardview_unselector);
                /**나눔 게시글 필터링*/
            }
        });
    }

    public void setPostWrite(){

        cv_postWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**글 쓰기 버튼 클릭 이벤트*/
                Intent intent = new Intent(getContext(),PostActivity.class);
                intent.putExtra("page",0);
                intent.putExtra("mode","write");
                intent.putExtra("category",category);
                startActivity(intent);
            }
        });
    }

    public void distanceSorting(){
        setRecyclerViewOption.sortPostItems();
    }

    public void fromMyPageOption(){
        fromMyPage = true;
    }

    public void setMyPostOption(){
        cv_postWrite = view.findViewById(R.id.cv_post);
        cv_postWrite.setClickable(false);
        cv_postWrite.setVisibility(View.INVISIBLE);
    }
}
