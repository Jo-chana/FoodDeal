package com.hankki.fooddeal.ui.mypage;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.staticdata.StaticUser;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ui.PopupActivity;

import java.io.InputStream;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**마이페이지 화면*/
public class MyPageFragment extends Fragment {

    ImageView arrow_my_post, arrow_notification, arrow_like, arrow_setting;
    ImageView iv_my_profile;
    TextView tv_my_name;
    Button btn_profile_revise, btn_logout;

    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_mypage, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            setGuestViewComponents();
        } else {
            setViewComponents();
        }
        return view;
    }
    public void setGuestViewComponents(){
        iv_my_profile = view.findViewById(R.id.iv_my_profile);
        iv_my_profile.setBackground(new ShapeDrawable(new OvalShape()));
        iv_my_profile.setClipToOutline(true);
        btn_profile_revise = view.findViewById(R.id.btn_profile_revise);
        btn_profile_revise.setClickable(false);
        ConstraintLayout ctl_option = view.findViewById(R.id.ctl_option);
        ctl_option.removeAllViews();
        Intent popupIntent = new Intent(getContext(), PopupActivity.class);
        startActivity(popupIntent);
    }

    public void setViewComponents(){

        iv_my_profile = view.findViewById(R.id.iv_my_profile);
        iv_my_profile.setBackground(new ShapeDrawable(new OvalShape()));
        iv_my_profile.setClipToOutline(true);

        tv_my_name = view.findViewById(R.id.tv_my_name);
        tv_my_name.setText(StaticUser.getName());
        btn_profile_revise = view.findViewById(R.id.btn_profile_revise);

        arrow_my_post = view.findViewById(R.id.arrow_my_post);
        arrow_notification = view.findViewById(R.id.arrow_notification);
        arrow_like = view.findViewById(R.id.arrow_like);
        arrow_setting = view.findViewById(R.id.arrow_setting);

        btn_profile_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tedPermission();
            }
        });

        arrow_my_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MyPostActivity.class);
                intent.putExtra("Mode","my_post");
                startActivity(intent);
            }
        });

        arrow_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MyPostActivity.class);
                intent.putExtra("Mode","like");
                startActivity(intent);
            }
        });

        arrow_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MySettingActivity.class);
                startActivity(intent);
            }
        });
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
                try {
                    InputStream in = getContext().getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    iv_my_profile.setImageBitmap(img);
                    iv_my_profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    /**내 프로필 사진 DB 반영*/
                    StaticUser.setProfile(img);

                    Toast.makeText(getContext(),"프로필 사진 수정 완료",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

}
