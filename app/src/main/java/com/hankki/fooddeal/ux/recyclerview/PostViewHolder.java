package com.hankki.fooddeal.ux.recyclerview;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;


/**Recycler View Post View Holder
 * Post Adapter 에서 Create & Bind*/
public class PostViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView mUserLocation;
    TextView mTitle;
    TextView mTime;
    ImageView mImage;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView.findViewById(R.id.view_list_community);
        mUserLocation = itemView.findViewById(R.id.tv_userlocation);
        mTitle = itemView.findViewById(R.id.tv_title);
        mTime = itemView.findViewById(R.id.tv_time);
        mImage = itemView.findViewById(R.id.iv_post_image);
    }
}
