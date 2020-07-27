package com.hankki.fooddeal.ui.home.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.retrofit.BoardController;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.security.AES256Util;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ui.address.AddressActivity;
import com.hankki.fooddeal.ux.dialog.CustomAnimationDialog;
import com.hankki.fooddeal.ux.recyclerview.AddressAdapter;
import com.hankki.fooddeal.ux.recyclerview.CommentAdapter;
import com.hankki.fooddeal.ux.viewpager.GalleryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class Community_detail extends AppCompatActivity implements OnMapReadyCallback {
    ViewPager vp_image; // 이미지 뷰페이저
    TabLayout tl_dots;
    GalleryAdapter galleryAdapter; // 뷰페이저 어댑터
    View topToolbar, myPostBottomToolbar, myCommentBottomToolbar;
    ConstraintLayout post_common, bottomToolbar; // 게시글 몸통, 하단 툴바(채팅 및 댓글창)
    ImageView profile; // 유저 프로필
    TextView userLocation, mapLocation; // 유저 위치, 게시글 위치(교환/나눔)
    TextView userId, postInfo, postText, postLike; //아이디, 게시글 정보(시간, 장소 등), 관심도(찜, 좋아요)
    RecyclerView rv_comment; // 댓글 리사이클러 뷰
    Button btn_comment;
    EditText et_comment;
    ArrayList<CommentItem> commentItems; // 댓글 리스트
    PostItem mPost;

    ImageView iv_setting, iv_dot;
    LinearLayout ll_revise, ll_delete;
    FrameLayout fl_bottom;

    CommentAdapter mAdapter;
    Context mContext;

    NestedScrollView scrollView;

    ArrayList<Bitmap> postImages;

    String uid;
    int order;
    int page; // 교환나눔, 레시피, 자유
    String tag;
    boolean isMyPage = false; // 내 게시글이면 대댓글 처리 따로

    CameraUpdate cameraUpdate;
    GoogleMap mapPost;

    Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        postImages = new ArrayList<>();

        if (getIntent() != null) {
            Intent intent = getIntent();
            page = intent.getIntExtra("page", -1); //교환나눔, 레시피, 자유
            order = intent.getIntExtra("index", -1); //몇 번째 게시글인가?
            tag = intent.getStringExtra("Tag");
            mPost = intent.getParcelableExtra("item");
        }
        try {
            FirebaseAuth.getInstance().getCurrentUser().getUid();
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            uid = "";
        }

        switch (page) {
            case 0:
                setContentView(R.layout.post_exchange_share);
                setPostCommon();
                setExchangeSharePostDetail();

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
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

        if (mPost.getUserHashId().equals(uid)) {
            setMyPostBottomToolbar();
            isMyPage = true;
        }
        if (uid.equals(""))
            setGuestBottomToolbarOption();
    }

    @SuppressLint("SetTextI18n")
    public void setExchangeSharePostDetail() {
        /*지도 설정*/
        mapLocation = findViewById(R.id.tv_post_loc);
        String myLocation = mPost.getRegionFirst() + " " + mPost.getRegionSecond() + " " + mPost.getRegionThird();
        mapLocation.setText(myLocation);
        String category;
        switch (mPost.getCategory()) {
            case "INGREDIENT EXCHANGE":
                category = "식재교환";
                break;
            case "INGREDIENT SHARE":
                category = "식재나눔";
                break;
            default:
                category = mPost.getCategory();
        }
        postInfo.setText(category + " ･ " + mPost.getInsertDate());

        Button btn_chat = bottomToolbar.findViewById(R.id.btn_chatting);
        /*@TODO 이현준 채팅방 생성*/
    }

    public void setRecipeFreePostDetail() {
        /*댓글 설정*/
        rv_comment = findViewById(R.id.rv_comment);
        postInfo.setText(mPost.getInsertDate());
        scrollView = findViewById(R.id.scroll);
        rv_comment.setNestedScrollingEnabled(false);

        et_comment = bottomToolbar.findViewById(R.id.et_comment);
        btn_comment = bottomToolbar.findViewById(R.id.btn_comment);
        defaultWriteComment();
        setCommentList();

        scrollView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                v.postDelayed(() -> {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    et_comment.requestFocus();
                }, 100);
            }
        });
    }

    public void defaultWriteComment() {
        btn_comment.setOnClickListener(v -> {
            String comment = et_comment.getText().toString();
            if (comment.equals(""))
                return;

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);

            CommentItem item = new CommentItem();
            item.setBoardSeq(mPost.getBoardSeq());
            item.setCommentContent(comment);
            item.setInsertDate(BoardController.getTime());

            if (BoardController.commentWrite(mContext, item)) {
                setCommentList();
                et_comment.setText(null);
            } else {
                Toast.makeText(mContext, "실패!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void writeChildComment(CommentItem parent) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        et_comment.requestFocus();

        btn_comment.setOnClickListener(v -> {
            String comment = et_comment.getText().toString();
            if (comment.equals(""))
                return;
            imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);

            CommentItem item = new CommentItem();
            item.setBoardSeq(mPost.getBoardSeq());
            item.setCommentContent(comment);
            item.setInsertDate(BoardController.getTime());
            item.setParentCommentSeq(parent.getCommentSeq());

            if (BoardController.childCommentWrite(mContext, parent, item)) {
                setCommentList();
                et_comment.setText(null);
            } else {
                Toast.makeText(mContext, "실패!", Toast.LENGTH_SHORT).show();
            }
            defaultWriteComment();
        });
    }

    @SuppressLint("SetTextI18n")
    public void setPostCommon() {
        vp_image = findViewById(R.id.vp_image);
        tl_dots = findViewById(R.id.tl_dots);

        topToolbar = findViewById(R.id.top_toolbar);
        bottomToolbar = findViewById(R.id.bottom_toolbar);
        CheckBox iv_like = bottomToolbar.findViewById(R.id.iv_like);
        /*내가 이미 찜한 게시글이면, iv_like 는 체크 상태 코드 추가
          if (mPost.isLiked == true) { iv_like.setChecked(true) }*/
        if (BoardController.isLikedBoard(mContext, mPost)) {
            iv_like.setChecked(true);
        }


        iv_like.setOnClickListener(v -> {
            /*유저가 기존에 찜한 게시글이 아닐 경우*/
            if (!(iv_like.isChecked())) {
                if (BoardController.boardLikeMinus(mContext, mPost)) {
                    iv_like.setChecked(false);
                    Toast.makeText(mContext, "게시글 찜을 취소했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "실패!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (BoardController.boardLikePlus(mContext, mPost)) {
                    iv_like.setChecked(true);
                    Toast.makeText(mContext, "이 게시글을 찜했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "실패!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        post_common = findViewById(R.id.post_common);

        profile = post_common.findViewById(R.id.iv_user_profile);
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);
        profile.setImageBitmap(mPost.getUserProfile());

        userId = post_common.findViewById(R.id.tv_user_id);
        userId.setText(AES256Util.aesDecode(mPost.getUserHashId()));

        userLocation = post_common.findViewById(R.id.tv_user_location);
        userLocation.setText(mPost.getRegionSecond() + " " + mPost.getRegionFirst());

        postInfo = post_common.findViewById(R.id.tv_post_info);
        postText = post_common.findViewById(R.id.tv_post);
        postText.setText(mPost.getBoardContent());

        postLike = post_common.findViewById(R.id.tv_post_like);
        if (mPost.getLikeCount() > 0) {
            postLike.setText(String.valueOf(mPost.getLikeCount()) + " 명이 찜했어요");
        }

        setBroadPostImages(mPost.getInsertDate());
    }

    private void setBroadPostImages(String date) {
        disposable = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for(int i=0;i<4;i++) {
                    StorageReference downloadImageRef = FirebaseStorage.getInstance().getReference().child("PostPhotos/" + date + "/" + Integer.toString(i) + ".jpg");

                    final long MAX_SIZE = 1024 * 1024 * 15;
                    Task<byte[]> task = downloadImageRef.getBytes(MAX_SIZE);
                    try {
                        byte[] imageBytes = Tasks.await(task);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        postImages.add(bitmap);
                        Log.e("#########", "postImages에 비트맵 추가");
                    } catch (Exception e) {
                        Log.e("#########", e.toString());
                    }
                }

                return false;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object result) throws Exception {
                        disposable.dispose();
                        setImageViewPager();
                        Log.e("#########", "setImageViewPager 실행");
                    }
                });
    }

    public void setMyPostBottomToolbar() {
        fl_bottom = findViewById(R.id.fl_bottom);
        fl_bottom.removeView(bottomToolbar);
        myPostBottomToolbar = View.inflate(this, R.layout.bottom_mypost, null);
        ll_revise = myPostBottomToolbar.findViewById(R.id.ll_revise);
        ll_delete = myPostBottomToolbar.findViewById(R.id.ll_delete);
        iv_dot = myPostBottomToolbar.findViewById(R.id.iv_dot);

        myCommentBottomToolbar = View.inflate(this, R.layout.bottom_my_comment, null);
        iv_setting = myCommentBottomToolbar.findViewById(R.id.iv_setting);
        et_comment = myCommentBottomToolbar.findViewById(R.id.et_comment);
        btn_comment = myCommentBottomToolbar.findViewById(R.id.btn_comment);

        if (page != 0) { // 레시피 자유
            fl_bottom.addView(myCommentBottomToolbar);
            postSetting();
            defaultWriteComment();
            scrollView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                if (bottom < oldBottom) {
                    v.postDelayed(() -> {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        et_comment.requestFocus();
                    }, 100);
                }
            });
        } else {
            fl_bottom.addView(myPostBottomToolbar);
            postRevise();
            postDelete();
        }
    }

    public void postSetting() {
        iv_setting.setOnClickListener(v -> {
            fl_bottom.removeView(myCommentBottomToolbar);
            fl_bottom.addView(myPostBottomToolbar);
        });
        iv_dot.setOnClickListener(v1 -> {
            fl_bottom.removeView(myPostBottomToolbar);
            fl_bottom.addView(myCommentBottomToolbar);
            postSetting();
            defaultWriteComment();
        });
        postRevise();
        postDelete();
    }

    public void postRevise() {
        ll_revise.setOnClickListener(v -> {
            Intent reviseIntent = new Intent(mContext, PostActivity.class);
            reviseIntent.putExtra("mode", "revise");
            reviseIntent.putExtra("page", page);
            reviseIntent.putExtra("item", mPost);
            startActivity(reviseIntent);
            finish();
            finish();
        });
    }

    public void postDelete() {
        ll_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("글을 삭제 하시겠습니까?");
            builder.setPositiveButton("확인", (dialog, which) -> {
                if (BoardController.boardDelete(mContext, mPost)) {
                    Toast.makeText(mContext, "글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                    if (tag.equals("Main")) {
                        /*게시글 리스트로 돌아갈 경우 변경사항 즉각 Update*/
                        NavHostFragment navHostFragment = (NavHostFragment) ((MainActivity) MainActivity.mainContext)
                                .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                        assert navHostFragment != null;
                        List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments().get(0)
                                .getChildFragmentManager().getFragments();

                        Fragment fragment = fragments.get(page);
                        switch (page) {
                            case 0:
                                ((ExchangeAndShare) fragment).setRecyclerView();
                                break;
                            case 1:
                                ((RecipeShare) fragment).setRecyclerView();
                                break;
                            case 2:
                                ((FreeCommunity) fragment).setRecyclerView();
                                break;
                        }
                    }
                } else {
                    Toast.makeText(mContext, "실패!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }).setNegativeButton("취소", (dialog, which) -> {
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    public void setGuestBottomToolbarOption() {
        FrameLayout fl_bottom = findViewById(R.id.fl_bottom);
        fl_bottom.removeView(bottomToolbar);
    }

    public void setImageViewPager() {
        ConstraintLayout main = findViewById(R.id.main_layout);
        if (postImages == null) {
            main.removeView(vp_image);
            View view = findViewById(R.id.trick);
            view.getLayoutParams().height = 120;
            return;
        } else if (postImages.size() == 0) {
            main.removeView(vp_image);
            View view = findViewById(R.id.trick);
            view.getLayoutParams().height = 120;
            return;
        }
        galleryAdapter = new GalleryAdapter(this, postImages);
        tl_dots.setupWithViewPager(vp_image, true);
        vp_image.setAdapter(galleryAdapter);
    }

    public void setCommentList() {
        commentItems = BoardController.getBoardCommentList(mPost);
        mAdapter = new CommentAdapter(commentItems);
        mAdapter.setChildCommentList();
        mAdapter.setContext(mContext);
        mAdapter.setIsMyPage(isMyPage);
        rv_comment.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rv_comment.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        if (tag.equals("Main")) {
            /*게시글 리스트로 돌아갈 경우 변경사항 즉각 Update*/
            NavHostFragment navHostFragment = (NavHostFragment) ((MainActivity) MainActivity.mainContext)
                    .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            assert navHostFragment != null;
            List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments().get(0)
                    .getChildFragmentManager().getFragments();

            Fragment fragment = fragments.get(page);
            switch (page) {
                case 0:
                    ((ExchangeAndShare) fragment).setRecyclerView();
                    break;
                case 1:
                    ((RecipeShare) fragment).setRecyclerView();
                    break;
                case 2:
                    ((FreeCommunity) fragment).setRecyclerView();
                    break;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /**지도 설정*/
        mapPost = googleMap;

        double latitude = Double.parseDouble(mPost.getUserLatitude());
        double longitude = Double.parseDouble(mPost.getUserLongitude());

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        postImages = null;
        disposable = null;
    }
}
