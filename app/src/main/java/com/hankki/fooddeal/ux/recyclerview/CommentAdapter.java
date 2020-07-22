package com.hankki.fooddeal.ux.recyclerview;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.staticdata.StaticUser;


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
        holder.iv_profile.setImageBitmap(StaticUser.getProfile());
        holder.iv_profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public int getItemCount() {
        return commentItems.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView tv_username;
        TextView tv_message;
        TextView tv_time;
        TextView tv_reply, tv_btn_reply;
        ImageView iv_profile;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_comment_message);
            tv_username = itemView.findViewById(R.id.tv_comment_user_name);
            tv_time = itemView.findViewById(R.id.tv_comment_time);
            tv_reply = itemView.findViewById(R.id.tv_reply);
            tv_btn_reply = itemView.findViewById(R.id.tv_btn_reply);
            iv_profile = itemView.findViewById(R.id.iv_comment_user_profile);
            iv_profile.setBackground(new ShapeDrawable(new OvalShape()));
            iv_profile.setClipToOutline(true);
        }
    }
}
