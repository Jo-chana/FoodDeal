package com.hankki.fooddeal.ux.recyclerview;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;


/**Recycler View Post View Holder
 * Post Adapter 에서 Create & Bind*/
public class PostViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView mUserName;
    TextView mUserLocation;
    TextView mTitle;
    TextView mPost;
    TextView mTime;
    Button btn_revise;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView.findViewById(R.id.view_list_community);
        mUserName = itemView.findViewById(R.id.tv_username);
        mUserLocation = itemView.findViewById(R.id.tv_userlocation);
        mTitle = itemView.findViewById(R.id.tv_title);
        mPost = itemView.findViewById(R.id.tv_post);
        mTime = itemView.findViewById(R.id.tv_time);
        btn_revise = itemView.findViewById(R.id.btn_item);
        mPost.getEllipsize();
    }
}
