package com.hankki.fooddeal.ux.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.ui.home.community.Community_detail;
import com.hankki.fooddeal.ui.home.community.PostActivity;

import java.util.ArrayList;
import java.util.Collections;


/**Recycler View Post Adapter*/
public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private Context mContext;
    ArrayList<PostItem> postItems;
    private int layout;
    PostViewHolder postViewHolder;
    int page;

    public PostAdapter(Context context, ArrayList<PostItem> itemList, int layout){
        mContext = context;
        postItems = itemList;
        this.layout = layout; // inflate 할 layout 받아와야 함.
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View baseView = View.inflate(mContext, layout ,null);
        postViewHolder = new PostViewHolder(baseView);

        return postViewHolder;
    }

    /**Layout 과 View Holder Binding.
     * 데이터에 따라 변수 바인딩하는 로직 추가할 것*/
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem item = postItems.get(position);
        setCommunityItem(holder, item);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.mView.getContext(), Community_detail.class);
                intent.putExtra("page",page);
                intent.putExtra("index",position);
                holder.mView.getContext().startActivity(intent);
            }
        });
        holder.btn_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.mView.getContext(), PostActivity.class);
                intent.putExtra("page",page);
                intent.putExtra("mode","revise");
                intent.putExtra("index",position);
                holder.mView.getContext().startActivity(intent);
            }
        });
    }

    /**총 게시글/채팅방 수.*/
    @Override
    public int getItemCount() {
        return postItems.size();
    }

    public void setCommunityItem(PostViewHolder holder, PostItem item){
        holder.mTitle.setText(item.getUserTitle()); // 수정해야 함! 테스트용
        holder.mUserLocation.setText(String.valueOf(item.getDistance())+"m");
        holder.mUserName.setText(item.getUserName());
        holder.mPost.setText(item.getUserPost());
        holder.mTime.setText(item.getUserTime());
    }

    public void setPage(int page){
        this.page = page;
    }

    public void distanceFitering(int distance){

    }

    public void distanceSorting(int distance){
        Collections.sort(postItems);
    }

    public void setPostItems(ArrayList<PostItem> postItems){
        this.postItems = postItems;
    }
}
