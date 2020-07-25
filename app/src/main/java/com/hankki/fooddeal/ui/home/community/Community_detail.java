package com.hankki.fooddeal.ui.home.community;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.retrofit.APIInterface;
import com.hankki.fooddeal.data.staticdata.StaticChatRoom;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.data.staticdata.StaticUser;
import com.hankki.fooddeal.ux.itemtouchhelper.ChatRoomItem;
import com.hankki.fooddeal.ux.recyclerview.CommentAdapter;
import com.hankki.fooddeal.ux.viewpager.GalleryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.disposables.Disposable;

public class Community_detail extends AppCompatActivity implements OnMapReadyCallback {
    ViewPager vp_image; // 이미지 뷰페이저
    TabLayout tl_dots;
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

    ArrayList<Bitmap> postImages;

    int order;
    int page;
    String tag;

    ArrayList<String> addressList = new ArrayList<String>();
    ArrayList<String> region1depthAddressList = new ArrayList<String>();
    ArrayList<String> region2depthAddressList = new ArrayList<String>();
    ArrayList<String> region3depthAddressList = new ArrayList<String>();

    Disposable disposable;
    APIInterface apiInterface;
    LatLng mPosition;
    CameraUpdate cameraUpdate;
    GoogleMap mapPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if(getIntent()!=null) {
            Intent intent = getIntent();
            page = intent.getIntExtra("page", -1); //교환나눔, 레시피, 자유
            order = intent.getIntExtra("index", -1); //몇 번째 게시글인가?
            tag = intent.getStringExtra("Tag");
        }

        setPostOption();

        switch (page) {
            case 0:
                setContentView(R.layout.post_exchange_share);
                setPostCommon();
                setExchangeSharePostDetail();

                SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.map_post);
                mapFragment.getMapAsync(this);

                break;
            case 1:
            case 2:
            default:
                setContentView(R.layout.post_recipe_free);
                setPostCommon();
                setRecipeFreePostDetail();
                break;
        }
        if(StaticUser.getPagedPosts(StaticUser.getMyPosts(),page).contains(mPost)){
            setMyPostBottomToolbar();
        }
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            setGuestBottomToolbarOption();
    }

    public void setPostOption(){
        switch (tag){
            case "My":
                mPost = StaticUser.getPagedPosts(StaticUser.getMyPosts(),page).get(order);
                break;
            case "Dib":
                mPost = StaticUser.getPagedPosts(StaticUser.getLikedPosts(),page).get(order);
                break;
            case "Main":
            default:
                mPost = staticPost.getPost(page,order);
                break;
        }
    }

    public void setExchangeSharePostDetail(){
        postInfo.setText(mPost.getCategory() + " ･ " + mPost.getInsertDate());

        Button btn_chat = bottomToolbar.findViewById(R.id.btn_chatting);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoomItem item = new ChatRoomItem(mPost.getBoardTitle(), page);
                item.setInformation("새로운 채팅방이 개설되었습니다. 이야기를 나눠보세요!");
                StaticChatRoom staticChatRoom = new StaticChatRoom();
                int index = staticChatRoom.getChatItems().size();
                staticChatRoom.addChatItem(item);
                Intent intent = new Intent();
                intent.putExtra("Chat",1);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });
    }

    public void setRecipeFreePostDetail(){
        /**댓글 설정*/
        rv_comment = findViewById(R.id.rv_comment);
        postInfo.setText(mPost.getInsertDate());
        scrollView = findViewById(R.id.scroll);
        rv_comment.setNestedScrollingEnabled(false);

        EditText et_comment = bottomToolbar.findViewById(R.id.et_comment);
        Button btn_comment = bottomToolbar.findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = et_comment.getText().toString();
                if(comment.equals(""))
                    return;

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_comment.getWindowToken(),0);

                CommentItem item = new CommentItem(StaticUser.getName(),comment,getTime(),StaticUser.getProfile());

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
                            et_comment.requestFocus();
                        }
                    }, 100);
                }
            }
        });
    }

    public void setPostCommon(){
        vp_image = findViewById(R.id.vp_image);
        tl_dots = findViewById(R.id.tl_dots);

        topToolbar = findViewById(R.id.top_toolbar);
        bottomToolbar = findViewById(R.id.bottom_toolbar);
        CheckBox iv_like = bottomToolbar.findViewById(R.id.iv_like);
        /**내가 이미 찜한 게시글이면, iv_like 는 체크 상태 코드 추가
         * if (mPost.isLiked == true) { iv_like.setChecked(true) }*/
        if(StaticUser.getPagedPosts(StaticUser.getLikedPosts(),page).contains(mPost))
            iv_like.setChecked(true);


        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like_count = mPost.getLikeCount();
                /**유저가 기존에 찜한 게시글이 아닐 경우*/
                if(!(iv_like.isChecked())){
                    mPost.setLikeCount(like_count - 1);
                    Toast.makeText(mContext,"게시글 찜을 취소했습니다.",Toast.LENGTH_SHORT).show();
                    StaticUser.getPagedPosts(StaticUser.getLikedPosts(),page).remove(mPost);
                } else {
                    iv_like.setChecked(true);
                    /**찜 버튼 빈 하트에서 꽉찬 하트로 변경 코드 이곳에*/
                    /**마이페이지 찜 페이지에 추가*/
                    mPost.setLikeCount(like_count + 1);
                    Toast.makeText(mContext, "이 게시글을 찜했습니다.", Toast.LENGTH_SHORT).show();
                    StaticUser.getPagedPosts(StaticUser.getLikedPosts(),page).add(mPost);
                }
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
        postText.setText(mPost.getBoardContent());

        postLike = post_common.findViewById(R.id.tv_post_like);

        commentItems = mPost.getComments();
        postImages = mPost.getImages();
        setImageViewPager();
    }

    public void setMyPostBottomToolbar(){
        FrameLayout fl_bottom = findViewById(R.id.fl_bottom);
        fl_bottom.removeView(bottomToolbar);
        View myPostBottomToolbar = View.inflate(this,R.layout.bottom_mypost, null);
        fl_bottom.addView(myPostBottomToolbar);

        TextView tv_revise = myPostBottomToolbar.findViewById(R.id.tv_revise);
        TextView tv_delete = myPostBottomToolbar.findViewById(R.id.tv_delete);

        tv_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviseIntent = new Intent(mContext,PostActivity.class);
                reviseIntent.putExtra("mode","revise");
                reviseIntent.putExtra("page",page);
                reviseIntent.putExtra("index",order);
                reviseIntent.putExtra("PageFromTag",tag);
                startActivity(reviseIntent);
                finish();
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                builder.setMessage("글을 삭제 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StaticUser.getPagedPosts(StaticUser.getMyPosts(),page).remove(mPost);
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    public void setGuestBottomToolbarOption(){
        FrameLayout fl_bottom = findViewById(R.id.fl_bottom);
        fl_bottom.removeView(bottomToolbar);
    }

    public void setImageViewPager(){
        ConstraintLayout main = findViewById(R.id.main_layout);
        if(postImages==null) {
            main.removeView(vp_image);
            View view = findViewById(R.id.trick);
            view.getLayoutParams().height=120;
            return;
        } else if (postImages.size()==0){
            main.removeView(vp_image);
            View view = findViewById(R.id.trick);
            view.getLayoutParams().height=120;
            return;
        }
        galleryAdapter = new GalleryAdapter(this,postImages);
        tl_dots.setupWithViewPager(vp_image,true);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /**지도 설정*/
        mapPost = googleMap;

        mapLocation = findViewById(R.id.tv_post_loc);

        double latitude = mPost.getLatitude();
        double longitude = mPost.getLongitude();

        LatLng latlng = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);

        mapPost.addMarker(markerOptions);

        CircleOptions circle1KM = new CircleOptions().center(latlng) //원점
                .radius(100)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#88ffb5c5")); //배경색
        mapPost.addCircle(circle1KM);


        cameraUpdate = CameraUpdateFactory.newLatLng(latlng);
        mapPost.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
    }
}
