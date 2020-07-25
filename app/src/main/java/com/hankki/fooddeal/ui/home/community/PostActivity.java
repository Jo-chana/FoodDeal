package com.hankki.fooddeal.ui.home.community;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.retrofit.BoardController;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ux.dialog.CustomDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    int page, order;
    String pageFromTag;

    CustomDialog customDialog;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_post);
        intent = getIntent();
        tag = "exchange";
        page = intent.getIntExtra("page",-1);
        order = intent.getIntExtra("index",-1);
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

        if (intent.getStringExtra("mode").equals("revise")) {
            toolbarTextView.setText("수정하기");
            setPostRevise();
        } else {
            toolbarTextView.setText("글쓰기");
            setImageWrite();
            setPostWrite();
        }
    }


    public void setExchangeAndShareComponents(){
            select_exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_exchange.setBackgroundResource(R.drawable.textview_selector);
                    select_exchange.setTextColor(getResources().getColor(R.color.original_primary));
                    select_share.setBackgroundResource(R.drawable.textview_unselector);
                    select_share.setTextColor(getResources().getColor(R.color.outFocus));
                    tag = "exchange";
                }
            });
            select_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_share.setBackgroundResource(R.drawable.textview_selector);
                    select_share.setTextColor(getResources().getColor(R.color.original_primary));
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
                    PopupMenu p = new PopupMenu(mContext, v);
                    p.getMenuInflater().inflate(R.menu.post_photo_menu, p.getMenu());
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.gallery:
                                    tedPermission();
                                    return true;
                                case R.id.camera:
                                    dispatchTakePictureIntent();
                                    return true;
                                case R.id.cancel:
                                    return true;
                            }
                            return false;
                        }
                    });
                    p.show();
                }
            });
        }
    }

    public void setPostWrite(){
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page==0&&(postImages.size()==0 || et_title.getText().toString().equals(""))){
                    customDialog = new CustomDialog(mContext,"사진과 제목은 필수 입력 사항입니다!");
                    customDialog.setCanceledOnTouchOutside(false);
                    customDialog.show();

                } else if(et_title.getText().toString().equals("")) {
                    customDialog = new CustomDialog(mContext,"제목은 필수 입력 사항입니다!");
                    customDialog.setCanceledOnTouchOutside(false);
                    customDialog.show();

                } else {
                    PostItem item = new PostItem();
                    item.setInsertDate(BoardController.getTime());
                    item.setBoardContent(et_post.getText().toString());
                    item.setBoardTitle(et_title.getText().toString());
                    item.setCategory(category);

                    /**테스트*/
                    PreferenceManager.setString(mContext,"Latitude","37.4758562");
                    PreferenceManager.setString(mContext,"Longitude","127.1482274");

                    if(BoardController.boardWrite(mContext,item)){
                        Toast.makeText(mContext, "게시글을 작성하였습니다", Toast.LENGTH_SHORT).show();
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

                    } else {
                        Toast.makeText(mContext,"실패!",Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        });
    }

    public void setPostRevise(){
        PostItem mPost = intent.getParcelableExtra("item");

        btn_write.setText("수정하기");
        et_title.setText(mPost.getBoardTitle());
        et_post.setText(mPost.getBoardContent());
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPost.setBoardTitle(et_title.getText().toString());
                mPost.setBoardContent(et_post.getText().toString());

                if(BoardController.boardRevise(mContext, mPost)){
                    Toast.makeText(mContext, "수정을 완료하였습니다", Toast.LENGTH_SHORT).show();
                    /**게시글 수정 후, 해당 커뮤니티에서 즉각적으로 Update*/
                    try {
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
                    } catch (Exception e){
                        /**내가 쓴 글 페이지에서 수정할 경우
                         * 위의 코드는 Out of Index Error
                         * @TODO 새로고침 코드로 대체 등 해결방법 강구*/
                    }
                } else {
                    Toast.makeText(mContext, "실패!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {

                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    postImages.add(img);
                    /**이미지 Attach*/
                    onImageAttach();

                    Toast.makeText(this,"사진 업로드 완료",Toast.LENGTH_SHORT).show();

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                File file = new File(currentPhotoPath);
                Bitmap img = MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.fromFile(file));
                if(img != null){
                    ExifInterface ei = new ExifInterface(currentPhotoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation) { /**이미지 원본에 맞게 회전변환*/

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(img, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(img, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(img, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = img;
                    }
                    postImages.add(rotatedBitmap);
                    onImageAttach();
                    Toast.makeText(mContext,"사진 촬영 업로드 완료",Toast.LENGTH_SHORT).show();
//                    galleryAddPic(file);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
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
                .setRationaleMessage("사진을 업로드하기 위하여 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    /**Continue only if the File was successfully created*/
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(mContext,
                                "com.hankki.fooddeal.FileProvider",
                                photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(mContext,"권한이 거부되어 있어요",Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진을 촬영하기 위하여 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setPermissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    /**카메로 촬영한 영상 이미지 처리
     * 기존 Bitmap 으로 가져온 이미지는 해상도 낮음
     * -> 원본 파일 접근 및 처리 함수 추가*/
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /*   prefix   */
                ".jpg",   /*   suffix   */
                storageDir      /*  directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void galleryAddPic(File file) { /**사진 저장*/
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }






    /*Date date = new Date(System.currentTimeMillis());
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
	    format.setTimeZone(time);

	    bufW.write(format.format(date) + "\n");*/
    /**
    이현준
     Firebase Storage에 등록하고 얻은 Uri들을 FireStore의 PostPhotos 컬렉션에다가 글이 등록된 시간별로 분류된 문서 안에 List를 저장
     (시간을 밀스초단위로 쪼개서 저장하는게 좋을듯)
     각각의 파일마다 이 함수 한번씩 써야함 (Firebase 파일 업로드 기능에 여러개를 보내는게 없음)
     */
    private void uploadPostPhoto(byte[] imageData, String time, Integer index) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("PostPhotos/" + time + "/" + index + ".jpg");
        UploadTask uploadTask = storageReference.putBytes(imageData);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    setPhotoUrlInFireStore(downloadUri, time, index);
                } else {
                    Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // postPhotos -> 현재 시각(서울 기준으로 ms 까지) -> Map<Integer, Uri> 형식으로 저장
    private void setPhotoUrlInFireStore(Uri photoUri, String time, Integer index) {
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("postPhotos").document(time);

        Map<Integer, Uri> photoUriMap = new HashMap<>();
        photoUriMap.put(index, photoUri);

        documentReference
                .set(photoUriMap, SetOptions.merge()) // 병합 옵션
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "이미지 URL FireStore 등록 성공", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "이미지 URL FireStore 등록 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}