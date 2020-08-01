package com.hankki.fooddeal.ui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.security.AES256Util;
import com.hankki.fooddeal.ui.home.community.Community_detail;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ArrayList<PostItem> postItems = new ArrayList<>();
    private ArrayList<PostItem> mapItems = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    VerticalSeekBar seekBar;
    TextView tv_100, tv_200, tv_400, tv_all;
    int filterDistance = 1000;
    int zoom = 14;
    Context mContext;
    LatLng currentPostion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mContext = this;
        postItems = getIntent().getParcelableArrayListExtra("Items");
        mapItems = postItems;
        findViews();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (int)(progress/100) * 100;
                seekBar.setProgress(progress);
                switch(progress){
                    case 0:
                        filterDistance = 1000;
                        zoom = 14;
                        tv_all.setTextColor(getResources().getColor(R.color.original_primary));
                        tv_100.setTextColor(getResources().getColor(R.color.original_black));
                        tv_200.setTextColor(getResources().getColor(R.color.original_black));
                        tv_400.setTextColor(getResources().getColor(R.color.original_black));
                        break;
                    case 100:
                        filterDistance = 400;
                        zoom = 15;
                        tv_all.setTextColor(getResources().getColor(R.color.original_black));
                        tv_100.setTextColor(getResources().getColor(R.color.original_black));
                        tv_200.setTextColor(getResources().getColor(R.color.original_black));
                        tv_400.setTextColor(getResources().getColor(R.color.original_primary));
                        break;
                    case 200:
                        filterDistance = 200;
                        zoom = 16;
                        tv_all.setTextColor(getResources().getColor(R.color.original_black));
                        tv_100.setTextColor(getResources().getColor(R.color.original_black));
                        tv_200.setTextColor(getResources().getColor(R.color.original_primary));
                        tv_400.setTextColor(getResources().getColor(R.color.original_black));
                        break;
                    case 300:
                        filterDistance = 100;
                        zoom = 17;
                        tv_all.setTextColor(getResources().getColor(R.color.original_black));
                        tv_100.setTextColor(getResources().getColor(R.color.original_primary));
                        tv_200.setTextColor(getResources().getColor(R.color.original_black));
                        tv_400.setTextColor(getResources().getColor(R.color.original_black));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mapItems = filterDistance(postItems, filterDistance);
                setMarkerOption(zoom);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void findViews(){
        tv_100 = findViewById(R.id.tv_100);
        tv_200 = findViewById(R.id.tv_200);
        tv_400 = findViewById(R.id.tv_400);
        tv_all = findViewById(R.id.tv_all);
        seekBar = findViewById(R.id.seekbar);
        seekBar.bringToFront();
        seekBar.setMax(300);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        double latitude = Double.parseDouble(AES256Util.aesDecode(PreferenceManager.getString(getApplicationContext(), "Latitude")));
        double longitude = Double.parseDouble(AES256Util.aesDecode(PreferenceManager.getString(getApplicationContext(), "Longitude")));

        currentPostion = new LatLng(latitude, longitude);
        setMarkerOption(15);


    }

    public void setMarkerOption(int zoom){
        map.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPostion);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_current));
        markerOptions.title("현재 위치");
        markerOptions.anchor((float)0.5,(float)0.5);
        markerOptions.snippet(PreferenceManager.getString(this, "region1Depth") + " " +
                PreferenceManager.getString(this, "region2Depth") + " " +
                PreferenceManager.getString(this, "region3Depth"));
        map.addMarker(markerOptions);



//        postItems.addAll(BoardController.getBoardList(mContext, "INGREDIENT EXCHANGE"));
//        postItems.addAll(BoardController.getBoardList(mContext,"INGREDIENT SHARE"));

        for (PostItem postItem : mapItems) {
            try {
                LatLng position = new LatLng(Double.parseDouble(postItem.getUserLatitude()), Double.parseDouble(postItem.getUserLongitude()));

                markerOptions = new MarkerOptions();
                markerOptions.position(position);

                int page;
                String category;
                if(postItem.getCategory().equals("RECIPE")) {
                    page = 1;
                    category = "레시피 게시판";
                }
                else if(postItem.getCategory().equals("FREE")) {
                    page = 2;
                    category = "자유 게시판";
                }
                else {
                    page = 0;
                    if(postItem.getCategory().equals("INGREDIENT EXCHANGE")) {
                        category = "식재 교환";
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_marker));
                    }
                    else {
                        category = "식재 나눔";
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_marker_2));
                    }
                }

                /*@TODO markerOption.icon 설정*/
                markerOptions.title(postItem.getBoardTitle());
                markerOptions.snippet(category);
                markers.add(map.addMarker(markerOptions));

            } catch (Exception ignored){

            }
        }

        map.setOnInfoWindowClickListener(marker -> {
            int index = markers.indexOf(marker);
            Intent intent = new Intent(MapActivity.this, Community_detail.class);
            intent.putExtra("page",0);
            intent.putExtra("Tag","Main");
            intent.putExtra("item",postItems.get(index));
            startActivity(intent);
        });

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPostion, zoom));
        CircleOptions circle1KM = new CircleOptions().center(currentPostion) // 원점
                .radius(filterDistance)      // 반지름 단위 : m
                .strokeWidth(0f)  // 선너비 0f : 선없음
                .fillColor(Color.parseColor("#88ffb5c5")); // 배경색
        map.addCircle(circle1KM);
    }

    public ArrayList<PostItem> filterDistance(ArrayList<PostItem> items, int distance){
        ArrayList<PostItem> filteredItems = new ArrayList<>();
        for(PostItem item: items){
            if(item.getDistance() <= distance){
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }
}
