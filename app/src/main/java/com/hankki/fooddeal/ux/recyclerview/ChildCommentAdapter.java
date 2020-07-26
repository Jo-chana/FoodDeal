package com.hankki.fooddeal.ux.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.security.AES256Util;

import java.util.ArrayList;

public class ChildCommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    ArrayList<CommentItem> childList;
    CommentViewHolder viewHolder;

    public ChildCommentAdapter(ArrayList<CommentItem> items){
        childList = items;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.child_comment_item,null);
        viewHolder = new CommentViewHolder(view, "Child");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem item = childList.get(position);
        holder.tv_message.setText(item.getCommentContent());
        holder.tv_time.setText(item.getDate());
        holder.tv_username.setText(AES256Util.aesDecode(item.getUserHashId()));
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }
}
