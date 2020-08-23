package com.hankki.fooddeal.ux.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.amazon.AmazonS3Util;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.retrofit.BoardController;
import com.hankki.fooddeal.data.security.AES256Util;

import java.util.ArrayList;

public class ChildCommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    ArrayList<CommentItem> childList;
    CommentViewHolder viewHolder;
    Context context;

    public ChildCommentAdapter(Context context, ArrayList<CommentItem> items){
        childList = items;
        this.context = context;
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
        holder.tv_time.setText(item.getRelativeTime());
        holder.tv_username.setText(AES256Util.aesDecode(item.getUserHashId()));
        String uid = item.getUserHashId();

        Glide.with(context).load(AmazonS3Util.s3.getUrl("hankki-s3","profile/"+uid).toString())
                .error(Glide.with(context).load(R.drawable.ic_group_60dp))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_profile);


        if(FirebaseAuth.getInstance().getCurrentUser()!=null&&!item.getUserHashId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.tv_btn_delete.setVisibility(View.GONE);
        } else {
            holder.tv_btn_delete.setOnClickListener(v -> {
                if(BoardController.commentDelete(context,item)){
                    Toast.makeText(context, "댓글을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    holder.commentView.setVisibility(View.GONE);
                    holder.commentView.getLayoutParams().height=0;
                } else {
                    Toast.makeText(context, "실패!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }
}
