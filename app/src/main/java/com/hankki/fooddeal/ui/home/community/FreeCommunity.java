package com.hankki.fooddeal.ui.home.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.data.staticdata.StaticUser;
import com.hankki.fooddeal.ux.recyclerview.SetRecyclerViewOption;

public class FreeCommunity extends Fragment {
    RecyclerView recyclerView;
    CardView cv_post;
    View view;
    SetRecyclerViewOption setRecyclerViewOption;
    StaticPost staticPost = new StaticPost();

    /**@Enum pageFrom {Main, My, Dib}*/
    String pageFrom = "Main";

    public FreeCommunity(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_free, container, false);
        if(pageFrom.equals("Main")) {
            setPostLists();
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
        setRecyclerViewOption.setPostItems(staticPost.getPostList(2));
        setRecyclerViewOption.build(2);
    }

    public void setPostLists(){
        staticPost.FreeDefault();
    }

    public void setPostWrite(){
        cv_post.setOnClickListener(new View.OnClickListener() {
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
    public void fromMyPageOption(String tag){
        pageFrom = tag;
    }

    public void setMyPostOption(){
        cv_post = view.findViewById(R.id.cv_post);
        cv_post.setClickable(false);
        cv_post.setVisibility(View.INVISIBLE);

        recyclerView = view.findViewById(R.id.rv_free);
        setRecyclerViewOption = new SetRecyclerViewOption(recyclerView, null,view,getContext(),R.layout.community_item);
        if(pageFrom.equals("My"))
            setRecyclerViewOption.setPostItems(StaticUser.getPagedPosts(StaticUser.getMyPosts(),2));
        else if (pageFrom.equals("Dib"))
            setRecyclerViewOption.setPostItems(StaticUser.getPagedPosts(StaticUser.getLikedPosts(),2));
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
