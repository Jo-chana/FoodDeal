package com.hankki.fooddeal.ux.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**Recycler View Post Adapter*/
public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private Context mContext;
    private ArrayList<PostItem> postItems;
    private int layout;
    PostViewHolder postViewHolder;
    public PostAdapter(Context context, ArrayList<PostItem> itemList, int layout){
        mContext = context;
        postItems = itemList;
        this.layout = layout; // inflate 할 layout 받아와야 함.
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View baseView = View.inflate(mContext, layout ,null);
        postViewHolder = new PostViewHolder(baseView, this);

        return postViewHolder;
    }

    /**Layout 과 View Holder Binding.
     * 데이터에 따라 변수 바인딩하는 로직 추가할 것*/
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem item = postItems.get(position);
    }

    /**총 게시글/채팅방 수.*/
    @Override
    public int getItemCount() {
        return postItems.size();
    }
}
