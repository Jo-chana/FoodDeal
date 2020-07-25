package com.hankki.fooddeal.ui.home.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.retrofit.BoardController;
import com.hankki.fooddeal.data.staticdata.StaticUser;
import com.hankki.fooddeal.ux.recyclerview.SetRecyclerViewOption;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FreeCommunity extends Fragment {
    RecyclerView recyclerView;
    CardView cv_post;
    View view;
    SetRecyclerViewOption setRecyclerViewOption;
    String category = "FREE";

    /**@Enum pageFrom {Main, My, Dib}*/
    String pageFrom = "Main";

    public FreeCommunity(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_free, container, false);
        if(pageFrom.equals("Main")) {
            setRecyclerView();
            setPostWrite();
        } else {
            setMyPostOption();
        }
        return view;
    }

    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_free);
        cv_post = view.findViewById(R.id.cv_post);
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            cv_post.setClickable(false);
            cv_post.setVisibility(View.INVISIBLE);
        }
        setRecyclerViewOption = new SetRecyclerViewOption(
                recyclerView, cv_post, view, getContext(), R.layout.community_item );
        setRecyclerViewOption.setPostItems(BoardController.getBoardList(getContext(),category));
        setRecyclerViewOption.setTag("Main");
        setRecyclerViewOption.build(2);
    }

    public void setPostWrite(){
        cv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**글 쓰기 버튼 클릭 이벤트*/
                Intent intent = new Intent(getContext(),PostActivity.class);
                intent.putExtra("mode","write");
                intent.putExtra("page",2);
                intent.putExtra("category","FREE");
                startActivity(intent);
            }
        });
    }
    public void distanceSorting(){
        setRecyclerViewOption.sortPostItems();
    }
    public void fromMyPageOption(String tag){
        pageFrom = tag;
    }

    public void setMyPostOption(){
        cv_post = view.findViewById(R.id.cv_post);
        cv_post.setClickable(false);
        cv_post.setVisibility(View.INVISIBLE);

        recyclerView = view.findViewById(R.id.rv_free);
        setRecyclerViewOption = new SetRecyclerViewOption(recyclerView, null,view,getContext(),R.layout.community_item);
        if(pageFrom.equals("My")) {
            setRecyclerViewOption.setPostItems(BoardController.getFreeBoardWriteList(getContext()));
        }
        else if (pageFrom.equals("Dib"))
            setRecyclerViewOption.setPostItems(BoardController.getFreeBoardLikeList(getContext()));
        setRecyclerViewOption.setTag(pageFrom);
        setRecyclerViewOption.build(2);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(pageFrom.equals("Main"))
            setRecyclerViewOption.update();
    }

}
