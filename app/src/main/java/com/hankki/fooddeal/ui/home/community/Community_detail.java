package com.hankki.fooddeal.ui.home.community;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticChatRoom;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.data.staticdata.StaticUser;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ux.itemtouchhelper.ChatRoomItem;
import com.hankki.fooddeal.ux.recyclerview.CommentAdapter;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.ux.viewpager.GalleryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Community_detail extends AppCompatActivity {
    ViewPager vp_image; // 이미지 뷰페이저
    GalleryAdapter galleryAdapter; // 뷰페이저 어댑터
    View topToolbar; // 상단 툴바,
    ConstraintLayout post_common, bottomToolbar; // 게시글 몸통, 하단 툴바(채팅 및 댓글창)
    ImageView profile; // 유저 프로필
    TextView userLocation, mapLocation; // 유저 위치, 게시글 위치(교환/나눔)
    TextView userId, postInfo, postText, postLike; //아이디, 게시글 정보(시간, 장소 등), 관심도(찜, 좋아요)
    RecyclerView rv_comment; // 댓글 리사이클러 뷰

    ArrayList<CommentItem> commentItems; // 댓글 리스트
    PostItem mPost;
    StaticPost staticPost = new StaticPost();
    CommentAdapter mAdapter;
    Context mContext;

    NestedScrollView scrollView;

    boolean myPost = false; // 자기 게시글이 아님

    ArrayList<Bitmap> postImages;

    int order;
    int page;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if(getIntent()!=null) {
            Intent intent = getIntent();
            page = intent.getIntExtra("page", -1); //교환나눔, 레시피, 자유
            order = intent.getIntExtra("index", -1); //몇 번째 게시글인가?
        }
        mPost = staticPost.getPost(page,order);

        switch (page) {
            case 0:
                setContentView(R.layout.post_exchange_share);
                setPostCommon();
                setExchangeSharePostDetail();
                break;
            case 1:
            case 2:
            default:
                setContentView(R.layout.post_recipe_free);
                setPostCommon();
                setRecipeFreePostDetail();
                break;
        }
    }

    public void setExchangeSharePostDetail(){
        /**지도 설정*/
        mapLocation = findViewById(R.id.tv_post_loc);
        postInfo.setText(mPost.getCategory()+" ･ "+mPost.getUserTime());

        Button btn_chat = bottomToolbar.findViewById(R.id.btn_chatting);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoomItem item = new ChatRoomItem(mPost.getUserTitle(), page);
                item.setInformation("새로운 채팅방이 개설되었습니다. 이야기를 나눠보세요!");
                StaticChatRoom staticChatRoom = new StaticChatRoom();
                int index = staticChatRoom.getChatItems().size();
                staticChatRoom.addChatItem(item);
                Intent intent = new Intent(Community_detail.this,MainActivity.class);
                intent.putExtra("Chat",1);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });
    }

    public void setRecipeFreePostDetail(){
        /**댓글 설정*/
        rv_comment = findViewById(R.id.rv_comment);
        postInfo.setText(mPost.getUserTime());
        scrollView = findViewById(R.id.scroll);
        rv_comment.setNestedScrollingEnabled(false);

        EditText et_comment = bottomToolbar.findViewById(R.id.et_comment);

        Button btn_comment = bottomToolbar.findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_comment.getWindowToken(),0);
                String comment = et_comment.getText().toString();
                StaticUser user = new StaticUser();
                CommentItem item = new CommentItem(user.getName(),comment,getTime(),user.getProfile());

                commentItems.add(commentItems.size(),item);
                mPost.setComments(commentItems);
                staticPost.updatePost(page,order,mPost);
                mAdapter.notifyDataSetChanged();
                et_comment.setText(null);
            }
        });
        setCommentList();
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom){
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }, 100);
                }
            }
        });
    }

    public void setPostCommon(){
        vp_image = findViewById(R.id.vp_image);

        bottomToolbar = findViewById(R.id.bottom_toolbar);
        ImageView iv_like = bottomToolbar.findViewById(R.id.iv_like);

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**유저가 기존에 찜한 게시글이 아닐 경우*/
                int like_count = mPost.getLike_count();
                mPost.setLike_count(like_count+1);
                /**찜 버튼 빈 하트에서 꽉찬 하트로 변경 코드 이곳에*/
                /**마이페이지 찜 페이지에 추가*/
                Toast.makeText(mContext,"이 게시글을 찜했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        post_common = findViewById(R.id.post_common);

        profile = post_common.findViewById(R.id.iv_user_profile);
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);
        profile.setImageBitmap(mPost.getUserProfile());

        userId = post_common.findViewById(R.id.tv_user_id);
        userId.setText(mPost.getUserName());

        userLocation = post_common.findViewById(R.id.tv_user_location);
        userLocation.setText(mPost.getUserLocation());

        postInfo = post_common.findViewById(R.id.tv_post_info);
        postText = post_common.findViewById(R.id.tv_post);
        postText.setText(mPost.getUserPost());

        postLike = post_common.findViewById(R.id.tv_post_like);

        commentItems = mPost.getComments();
        postImages = mPost.getImages();
        setImageViewPager();
    }

    public void setImageViewPager(){
        ConstraintLayout main = findViewById(R.id.main_layout);
        if(postImages==null) {
            main.removeView(vp_image);
            View view = findViewById(R.id.trick);
            view.getLayoutParams().height=30;
            return;
        } else if (postImages.size()==0){
            main.removeView(vp_image);
            return;
        }
        galleryAdapter = new GalleryAdapter(this,postImages);
        vp_image.setAdapter(galleryAdapter);
    }

    public void setCommentList(){
        mAdapter = new CommentAdapter(commentItems);
        rv_comment.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rv_comment.setAdapter(mAdapter);
    }

    public String getTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfnow = new SimpleDateFormat("MM/dd HH:mm");
        String timeData = sdfnow.format(date);
        return timeData;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
