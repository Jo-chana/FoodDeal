package com.hankki.fooddeal.ui.home.community;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.data.staticdata.StaticUser;
import com.hankki.fooddeal.ui.MainActivity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**게시글 쓰기 액티비티*/
public class PostActivity extends AppCompatActivity {
    EditText et_title;
    EditText et_post;
    LinearLayout select_location,ll_images, ll_choice, ll_post;
    Button btn_write;
    Intent intent;
    TextView select_exchange, select_share, toolbarTextView;
    View toolbarView;
    ImageView backButton;
    StaticPost staticPost = new StaticPost();
    int page, index;

    String category = ""; // 테스트용/ 교환인지 나눔인지

    ArrayList<Bitmap> postImages = new ArrayList<>();
    int[] imageResources = new int[]{R.id.image_1,R.id.image_2,R.id.image_3,R.id.image_4};
    int[] imageTexts = new int[]{R.id.tv_image_num,R.id.tv_image_num2, R.id.tv_image_num3, R.id.tv_image_num4};
    ImageView[] imageViews = new ImageView[4];
    TextView[] textViews = new TextView[4];
    String tag;

    Context mContext;

    //테스트

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_post);
        intent = getIntent();
        tag = "exchange";
        page = intent.getIntExtra("page",-1);
        index = intent.getIntExtra("index",-1);
        category = intent.getStringExtra("category");
        setIdComponents();
    }

    public void setIdComponents() {
        ll_post = findViewById(R.id.ll_post);
        et_title = findViewById(R.id.et_post_title);
        et_post = findViewById(R.id.et_post_post);
        select_exchange = findViewById(R.id.select_exchange);
        select_share = findViewById(R.id.select_share);
        select_location = findViewById(R.id.select_location);
        ll_images = findViewById(R.id.ll_images);
        ll_choice = findViewById(R.id.ll_choice);
        toolbarView = findViewById(R.id.post_toolbar);
        toolbarTextView = toolbarView.findViewById(R.id.toolbar_title);
        toolbarTextView.setText("글쓰기");
        backButton = toolbarView.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(page==0){
            setExchangeAndShareComponents();
        } else {
            setRecipeFreeComponents();
        }

        btn_write = findViewById(R.id.btn_post_write);

        for (int i = 0; i < 4; i++) {
            imageViews[i] = findViewById(imageResources[i]);
            textViews[i] = findViewById(imageTexts[i]);
        }
        textViews[0].setText("0/4");

        if (intent.getStringExtra("mode").equals("revise"))
            setPostRevise();
        else {
            setImageWrite();
            setPostWrite();
        }
    }


    public void setExchangeAndShareComponents(){
            select_exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_exchange.setBackgroundResource(R.drawable.textview_selector);
                    select_exchange.setTextColor(getResources().getColor(R.color.colorAccent));
                    select_share.setBackgroundResource(R.drawable.textview_unselector);
                    select_share.setTextColor(getResources().getColor(R.color.outFocus));
                    tag = "exchange";
                }
            });
            select_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_share.setBackgroundResource(R.drawable.textview_selector);
                    select_share.setTextColor(getResources().getColor(R.color.colorAccent));
                    select_exchange.setBackgroundResource(R.drawable.textview_unselector);
                    select_exchange.setTextColor(getResources().getColor(R.color.outFocus));
                    tag = "share";
                }
            });
        setLocation();
    }
    public void setRecipeFreeComponents(){
        ll_post.removeView(ll_choice);
        ll_post.removeView(select_location);
    }

    public void setLocation(){
        /**위치정보 입력*/
    }

    public void setImageWrite(){
        int image_size = postImages.size();

        if(image_size > 0){
            for(int i=0;i<image_size;i++){
                final int finalI = i;
                imageViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postImages.remove(finalI);
                        onImageAttach();
                    }
                });
            }
        }

        if(image_size < 4) {
            imageViews[image_size].setOnClickListener(new View.OnClickListener() {
                @Override
                /**이미지 삽입*/
                public void onClick(View v) {
                    tedPermission();
                }
            });
        }
    }

    public void setPostWrite(){
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page==0&&(postImages.size()==0 || et_title.getText().toString().equals(""))){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("사진과 제목은 필수 입력 항목입니다!");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if(et_title.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("제목은 필수 입력 항목입니다!");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdfnow = new SimpleDateFormat("MM/dd HH:mm");
                    String timeData = sdfnow.format(date);
                    int distance = 0;
                    StaticUser user = new StaticUser();
                    PostItem item = new PostItem(user.getName(), et_post.getText().toString(),
                            "비전동", et_title.getText().toString(), timeData,
                            distance, user.getProfile(), postImages, 127.1502261, 37.4749207);
                    item.setCategory(category);
                    staticPost.addPost(page, item); // 게시글 추가

                    /**게시글 추가 후, 해당 커뮤니티에서 즉각적으로 Update*/
                    NavHostFragment navHostFragment = (NavHostFragment) ((MainActivity) MainActivity.mainContext)
                            .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
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
                    finish();
                }
            }
        });
    }

    public void setPostRevise(){
        PostItem item = staticPost.getPost(page,index);
        btn_write.setText("수정하기");
        et_title.setText(item.getUserTitle());
        et_post.setText(item.getUserPost());
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item.setUserTitle(et_title.getText().toString());
                item.setUserPost(et_post.getText().toString());

                staticPost.updatePost(page,index,item);


                NavHostFragment navHostFragment = (NavHostFragment) ((MainActivity) MainActivity.mainContext)
                        .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments().get(0)
                        .getChildFragmentManager().getFragments();

                Fragment fragment = fragments.get(page);
                switch(page){
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
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    postImages.add(img);
                    /**이미지 Attach*/
                    onImageAttach();

                    Toast.makeText(this,"사진 업로드 완료",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onImageAttach(){
        for(int i=0;i<4;i++){
            if(i<postImages.size()){
                imageViews[i].setImageBitmap(postImages.get(i));
                textViews[i].setText(null);
            }
            else if(i==postImages.size()) {
                imageViews[i].setImageResource(R.drawable.camera_test);
                textViews[i].setText(String.valueOf(i)+"/4");
            }
            else{
                imageViews[i].setImageBitmap(null);
                textViews[i].setText(null);
            }
        }
        setImageWrite();
    }

    public void tedPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 0);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(mContext,"권한이 거부되어 있어요",Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setDeniedMessage("사진을 업로드하기 위하여 권한이 필요합니다.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

}