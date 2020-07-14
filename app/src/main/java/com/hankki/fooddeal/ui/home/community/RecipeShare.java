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

public class RecipeShare extends Fragment {
    View view;
    RecyclerView recyclerView;
    ActionButton fab;
    SetRecyclerViewOption setRecyclerViewOption;
    StaticPost staticPost = new StaticPost();

    public RecipeShare(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_recipe, container, false);
        setPostLists();
        setRecyclerView();
        setPostWrite();
        return view;
    }

    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_recipe);
        fab = view.findViewById(R.id.fab_write);
        setRecyclerViewOption = new SetRecyclerViewOption(
                recyclerView, fab, view, getContext(), R.layout.community_item );
        setRecyclerViewOption.setPostItems(staticPost.getPostList(1));
        setRecyclerViewOption.build(1);
    }

    public void setPostLists(){
        staticPost.RecipeShareDefault();
    }

    public void setPostWrite(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**글 쓰기 버튼 클릭 이벤트*/
                Intent intent = new Intent(getContext(),PostActivity.class);
                intent.putExtra("page",1);
                intent.putExtra("mode","write");
                startActivity(intent);
            }
        });
    }
    public void distanceSorting(){
        setRecyclerViewOption.sortPostItems();
    }
}
