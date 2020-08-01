package com.hankki.fooddeal.ui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

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
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.ui.home.community.Community_detail;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ArrayList<PostItem> postItems = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mContext = this;
        postItems = getIntent().getParcelableArrayListExtra("Items");

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        double latitude = Double.parseDouble(PreferenceManager.getString(getApplicationContext(), "Latitude"));
        double longitude = Double.parseDouble(PreferenceManager.getString(getApplicationContext(), "Longitude"));

        LatLng currentPostion = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPostion);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_current));
        markerOptions.title("현재 위치");
        markerOptions.anchor((float)0.5,(float)0.5);
        markerOptions.snippet(PreferenceManager.getString(this, "region1Depth") + " " +
                PreferenceManager.getString(this, "region2Depth") + " " +
                PreferenceManager.getString(this, "region3Depth"));
        map.addMarker(markerOptions);


        CircleOptions circle1KM = new CircleOptions().center(currentPostion) // 원점
                .radius(500)      // 반지름 단위 : m
                .strokeWidth(0f)  // 선너비 0f : 선없음
                .fillColor(Color.parseColor("#88ffb5c5")); // 배경색
        map.addCircle(circle1KM);

//        postItems.addAll(BoardController.getBoardList(mContext, "INGREDIENT EXCHANGE"));
//        postItems.addAll(BoardController.getBoardList(mContext,"INGREDIENT SHARE"));

        for (PostItem postItem : postItems) {
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

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPostion, 15));
    }

}
