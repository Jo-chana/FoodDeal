package com.hankki.fooddeal.ux.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.home.community.PostActivity;

public class CustomPostImageDialog extends BottomSheetDialog {
    Context context;
    Button btn_gallary, btn_camera, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_image_dialog);
        btn_gallary = findViewById(R.id.btn_gallary);
        btn_camera = findViewById(R.id.btn_camera);
        btn_cancel = findViewById(R.id.btn_cancel);

        btn_gallary.setOnClickListener(v -> {
            ((PostActivity) context).tedPermission();
            dismiss();
        });
        btn_camera.setOnClickListener(v -> {
            ((PostActivity) context).dispatchTakePictureIntent();
            dismiss();
        });
        btn_cancel.setOnClickListener(v -> {
            dismiss();
        });

    }

    public CustomPostImageDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }
}
