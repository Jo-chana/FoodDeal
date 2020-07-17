package com.hankki.fooddeal.ux.viewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hankki.fooddeal.R;

import java.util.ArrayList;

public class GalleryAdapter extends PagerAdapter {

    Context context = null;
    ArrayList<Bitmap> images;

    public GalleryAdapter(Context context, ArrayList<Bitmap> imageIds){
        this.context = context;
        images = imageIds;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View page = inflater.inflate(R.layout.gallery_image,null);
        ImageView iv = page.findViewById(R.id.iv_gallary);
        iv.setImageBitmap(images.get(position));
        container.addView(page,0);

        return page;
    }

    @Override
    public int getCount() {
        return (images == null)?0:images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
