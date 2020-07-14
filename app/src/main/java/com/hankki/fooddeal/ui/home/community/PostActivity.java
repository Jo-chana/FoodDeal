package com.hankki.fooddeal.ui.home.community;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticPost;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.data.PostItem;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**게시글 쓰기 액티비티*/
public class PostActivity extends AppCompatActivity {
    EditText et_title;
    EditText et_post;
    Button btn_location;
    Button btn_write;
    Button btn_image;
    Intent intent;
    StaticPost staticPost = new StaticPost();
    int page, index;

    ArrayList<Bitmap> postImages = new ArrayList<>();
    int[] imageResources = new int[]{R.id.image_1,R.id.image_2,R.id.image_3,R.id.image_4};
    ImageView[] imageViews = new ImageView[4];
    int imageIndex = 0;

    Context mContext;


    //테스트
    EditText et_distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_post);
        intent = getIntent();
        page = intent.getIntExtra("page",-1);
        index = intent.getIntExtra("index",-1);
        setIdComponents();
    }

    public void setIdComponents(){
        et_distance = findViewById(R.id.et_distance);
        et_title = findViewById(R.id.et_post_title);
        et_post = findViewById(R.id.et_post_post);
        btn_location = findViewById(R.id.btn_post_location);
        setLocation();
        btn_image = findViewById(R.id.btn_post_image);
        btn_write = findViewById(R.id.btn_post_write);

        for(int i=0;i<4;i++){
            imageViews[i] = findViewById(imageResources[i]);
        }

        if(intent.getStringExtra("mode").equals("revise"))
            setPostRevise();
        else
            setPostWrite();
    }

    public void setLocation(){
        /**위치정보 입력*/
    }

    public void setPostWrite(){
            btn_image.setOnClickListener(new View.OnClickListener() {
            @Override /**이미지 삽입*/
            public void onClick(View v) {
                if(imageIndex>3) {
                    Toast.makeText(mContext, "이미지는 최대 4장까지 올릴 수 있어요", Toast.LENGTH_SHORT).show();
                    return;
                }
                tedPermission();
            }
        });

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfnow = new SimpleDateFormat("MM/dd HH:mm");
                String timeData = sdfnow.format(date);
                int distance = 0;
                if(et_distance.getText().toString().equals("")) {
                }else{
                    distance = Integer.parseInt(et_distance.getText().toString());
                }
                PostItem item = new PostItem("익명",et_post.getText().toString(),
                        "비전동",et_title.getText().toString(),timeData,
                        distance, null, postImages);

                staticPost.addPost(page,item); // 게시글 추가

                /**게시글 추가 후, 해당 커뮤니티에서 즉각적으로 Update*/
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
                    for(int i=0;i<4;i++){
                        if(i>=postImages.size()){
                            imageViews[i].setImageBitmap(null); continue;
                        }
                        imageViews[i].setImageBitmap(postImages.get(i));
                        imageViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
                        int finalI = i;
                        imageViews[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                postImages.remove(finalI);
                                imageIndex-=1;
                                for(int i=0;i<4;i++) {
                                    if (i >= postImages.size()) {
                                        imageViews[i].setImageBitmap(null);
                                        continue;
                                    }
                                    imageViews[i].setImageBitmap(postImages.get(i));
                                    imageViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
                                }
                            }
                        });
                    }
                    imageIndex+=1;

                    Toast.makeText(this,"사진 업로드 완료",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
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