package com.hankki.fooddeal.ux.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.CommentItem;


import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    ArrayList<CommentItem> commentItems;
    CommentViewHolder viewHolder;

    public CommentAdapter(ArrayList<CommentItem> commentItems){
        this.commentItems = commentItems;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(),R.layout.comment_item,null);
        viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem item = commentItems.get(position);
        holder.tv_username.setText(item.getUserName());
        holder.tv_message.setText(item.getMessage());
        holder.tv_time.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return commentItems.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView tv_username;
        TextView tv_message;
        TextView tv_time;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_comment_message);
            tv_username = itemView.findViewById(R.id.tv_comment_user_name);
            tv_time = itemView.findViewById(R.id.tv_comment_time);
        }
    }
}
