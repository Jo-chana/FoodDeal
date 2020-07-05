package com.hankki.fooddeal.ux.recyclerview;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**Recycler View Post View Holder
 * Post Adapter 에서 Create & Bind*/
public class PostViewHolder extends RecyclerView.ViewHolder {
    private PostAdapter mAdapter;

    public PostViewHolder(@NonNull View itemView, PostAdapter adapter) {
        super(itemView);
        mAdapter = adapter;
    }
}
