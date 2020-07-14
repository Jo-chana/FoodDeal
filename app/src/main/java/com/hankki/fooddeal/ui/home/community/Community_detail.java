package com.hankki.fooddeal.ui.home.community;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticChatRoom;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ux.itemtouchhelper.ChatRoomItem;
import com.hankki.fooddeal.ux.recyclerview.CommentAdapter;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.PostItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Community_detail extends AppCompatActivity {
    ImageView iv_profile; // 프로필 사진
    RecyclerView rv_comment; // 댓글 창
    TextView tv_user_name; // 글쓴 유저 이름
    TextView tv_user_location; // 글쓴 유저 장소
    TextView tv_title; // 게시글 제목
    TextView tv_post; // 게시글 내용
    EditText et_comment; // 댓글 입력창
    Button btn_like; // 찜 버튼
    Button btn_chat; // 채팅하기 버튼
    Button btn_comment; // 댓글 남기기 버튼
    ImageButton btn_image; //이미지 업로드 버튼
    ArrayList<CommentItem> commentItems; // 댓글 리스트
    PostItem mPost;
    StaticPost staticPost = new StaticPost();
    CommentAdapter mAdapter;
    Context mContext;

    ArrayList<Bitmap> postImages;
    int[] imageViews = new int[]{R.id.image_1,R.id.image_2,R.id.image_3,R.id.image_4};
    int imageIndex = 0;

    int order;
    int page;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_detail);
        mContext = this;
        if(getIntent()!=null) {
            Intent intent = getIntent();
            page = intent.getIntExtra("page", -1); //교환나눔, 레시피, 자유
            order = intent.getIntExtra("index", -1); //몇 번째 게시글인가?
        }
        mPost = staticPost.getPost(page,order);


        setPostDetail();
        setCommentList();
        setOnClickListenerButtons();
    }

    public void setPostDetail(){
        iv_profile = findViewById(R.id.iv_userprofile); //글쓴이 프사
        tv_user_name = findViewById(R.id.tv_username); //글쓴이 이름
        tv_user_location = findViewById(R.id.tv_userlocation); //글쓴이 위치
        tv_title = findViewById(R.id.tv_title); //글 제목
        tv_post = findViewById(R.id.tv_userpost); //글 내용
        rv_comment = findViewById(R.id.rv_comment); //댓글 창
        et_comment = findViewById(R.id.et_comment); //댓글 입력 창
        btn_chat = findViewById(R.id.btn_chat); //채팅하기 버튼
        btn_like = findViewById(R.id.btn_like); //찜하기 버튼
        btn_comment = findViewById(R.id.btn_comment); //댓글 보내기 버튼
        btn_image = findViewById(R.id.btn_image); //댓글 이미지 삽입 버튼

        tv_user_name.setText(mPost.getUserName());
        tv_user_location.setText(mPost.getUserLocation());
        tv_title.setText(mPost.getUserTitle());
        tv_post.setText(mPost.getUserPost());
        commentItems = mPost.getComments();
        postImages = mPost.getImages();

        if(postImages!=null) {
            for (int i = 0; i < postImages.size(); i++) {
                ImageView imageView = findViewById(imageViews[i]);
                Bitmap imageBitmap = postImages.get(i);
                imageView.setImageBitmap(imageBitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override /**이미지 클릭했을 때 팝업창 띄워서 크게 보여줌*/
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(mContext);
                        dialog.setContentView(R.layout.post_image_detail);
                        dialog.setTitle("Image Detail");
                        ImageView image = dialog.findViewById(R.id.iv_image);
                        image.setImageBitmap(imageBitmap);

                        dialog.show();
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }

        rv_comment.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom){
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rv_comment.smoothScrollToPosition(commentItems.size());
                        }
                    }, 100);
                }
            }
        });
    }

    public void setCommentList(){
        mAdapter = new CommentAdapter(commentItems);
        rv_comment.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rv_comment.setAdapter(mAdapter);
    }

    public void setOnClickListenerButtons(){
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_comment.getWindowToken(),0);
                String comment = et_comment.getText().toString();
                CommentItem item = new CommentItem("댓쓴이",comment,getTime());

                commentItems.add(commentItems.size(),item);
                mPost.setComments(commentItems);
                staticPost.updatePost(page,order,mPost);
                mAdapter.notifyDataSetChanged();
                et_comment.setText(null);
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoomItem item = new ChatRoomItem(tv_title.getText().toString() ,page);
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

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });
    }

    public String getTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfnow = new SimpleDateFormat("MM/dd HH:mm");
        String timeData = sdfnow.format(date);
        return timeData;
    }
}
