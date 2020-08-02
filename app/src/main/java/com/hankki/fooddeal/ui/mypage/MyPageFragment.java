package com.hankki.fooddeal.ui.mypage;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.security.AES256Util;
import com.hankki.fooddeal.ux.dialog.CustomPostImageDialog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**마이페이지 화면*/
public class MyPageFragment extends Fragment {

    View view_my_post, view_notification, view_like, view_setting;
    ImageView arrow_my_post, arrow_notification, arrow_like, arrow_setting;
    ImageView iv_my_profile;
    TextView tv_my_name;
    Button btn_profile_revise;
    CustomPostImageDialog dialog;
    String uid;
    ProgressBar progressBar;
    Disposable disposable;

    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mypage, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = "";
        }

        progressBar = view.findViewById(R.id.customDialog_progressBar);

        setViewComponents();

        return view;
    }

    public void setViewComponents(){
        iv_my_profile = view.findViewById(R.id.iv_my_profile);
        iv_my_profile.setBackground(new ShapeDrawable(new OvalShape()));
        iv_my_profile.setClipToOutline(true);
        iv_my_profile.setScaleType(ImageView.ScaleType.CENTER_CROP);

        tv_my_name = view.findViewById(R.id.tv_my_name);
//        tv_my_name.setText(user.getName());
        btn_profile_revise = view.findViewById(R.id.btn_profile_revise);

        arrow_my_post = view.findViewById(R.id.arrow_my_post);
        arrow_notification = view.findViewById(R.id.arrow_notification);
        arrow_like = view.findViewById(R.id.arrow_like);
        arrow_setting = view.findViewById(R.id.arrow_setting);

        view_my_post = view.findViewById(R.id.view_my_post);
        view_notification = view.findViewById(R.id.view_notification);
        view_like = view.findViewById(R.id.view_like);
        view_setting = view.findViewById(R.id.view_setting);

        btn_profile_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new CustomPostImageDialog(getContext(), "my",galleryListener);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        view_my_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MyPostActivity.class);
                intent.putExtra("Mode","my_post");
                startActivity(intent);
            }
        });

        view_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MyPostActivity.class);
                intent.putExtra("Mode","like");
                startActivity(intent);
            }
        });

        view_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MySettingActivity.class);
                startActivity(intent);
            }
        });

        setUserProfile();
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
                Toast.makeText(getContext(),"권한이 거부되어 있어요",Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(getContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setDeniedMessage("사진을 업로드하기 위하여 권한이 필요합니다.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.bringToFront();
                disposable = Observable.fromCallable((Callable<Object>) () -> {
                    try {
                        InputStream in = getContext().getContentResolver().openInputStream(data.getData());

                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();

                        uploadUserProfilePhoto(imageData);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            disposable.dispose();
                        });

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadUserProfilePhoto(byte[] imageData) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("userPhoto/" + uid + ".jpg");
        UploadTask uploadTask = storageReference.putBytes(imageData);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(getContext(), "이미지 저장 실패", Toast.LENGTH_SHORT).show();
                }

                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    updateUserProfilePhoto(downloadUri);
                } else {
                    Toast.makeText(getContext(), "이미지 다운로드 URL 저장 실패", Toast.LENGTH_SHORT).show();
                }
                setUserProfile();
            }
        });
    }

    private void updateUserProfilePhoto(Uri uri) {
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);

        documentReference
                .update("userPhotoUri", uri.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
    }

    private void setUserProfile() {
        Glide
                .with(getContext())
                .load(R.drawable.ic_group_60dp)
                .into(iv_my_profile);

        if(!uid.equals("")) {
            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);
            documentReference
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(!documentSnapshot.get("userPhotoUri").equals("")) {

                                Glide
                                        .with(getContext())
                                        .load(documentSnapshot.get("userPhotoUri"))
                                        .into(iv_my_profile);
                            }
                            if(!documentSnapshot.get("userNickname").equals(""))
                                tv_my_name.setText(documentSnapshot.get("userNickname").toString());
                            else
                                tv_my_name.setText(AES256Util.aesDecode(uid));
                        }
                    });
        } else {
            tv_my_name.setText("게스트");
        }
    }

    public View.OnClickListener galleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tedPermission();
            dialog.dismiss();
        }
    };
}
