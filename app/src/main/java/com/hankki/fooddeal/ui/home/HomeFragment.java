package com.hankki.fooddeal.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ui.address.AddressActivity;
import com.hankki.fooddeal.ui.home.community.ExchangeAndShare;
import com.hankki.fooddeal.ui.home.community.FreeCommunity;
import com.hankki.fooddeal.ui.home.community.RecipeShare;
import com.hankki.fooddeal.ui.login.LoginActivity;
import com.hankki.fooddeal.ux.viewpager.viewPagerAdapter;

import java.util.List;

/**홈 화면*/
public class HomeFragment extends Fragment {
    ViewPager2 viewpager;
    Fragment[] fragments = new Fragment[3];
    TabLayout tabLayout;
    viewPagerAdapter viewPagerAdapter;
    View view;

    Button btn_search;
    AppBarLayout ctl_home;
    TextView tv_location;
    Button btn_filter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_home, container, false);
        btn_search = view.findViewById(R.id.btn_search);
        btn_filter = view.findViewById(R.id.btn_filter);

        setLocation();
        setFragments();
        setViewPager();
        setTabLayout();
//        filterButtonClickListener();
        return view;
    }

    public void setLocation(){
        tv_location = view.findViewById(R.id.tv_location);
        tv_location.setText(PreferenceManager.getString(getContext(),"Location"));
        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(getContext(),v);
                ((MainActivity)MainActivity.mainContext).getMenuInflater().inflate(R.menu.menu_location,p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
                p.show();
            }
        });
    }

    /**탭으로 구성할 Fragments 리스트*/
    public void setFragments(){
        fragments[0] = new ExchangeAndShare();
        fragments[1] = new RecipeShare();
        fragments[2] = new FreeCommunity();
    }

    /**View Pager -> ux.viewpager.viewPagerAdapter class*/
    public void setViewPager(){
        viewPagerAdapter = new viewPagerAdapter(this,fragments);
        viewpager = view.findViewById(R.id.vp_home);
        ctl_home = view.findViewById(R.id.ctl_home);
        viewpager.setAdapter(viewPagerAdapter);
    }

    /**상단 탭 바에 나타낼 Title 적용*/
    public void setTabLayout(){
        /**이부분은 따로 String Class 정의해서 사용하는 것이 나을 듯*/
        String[] names = new String[]{"식재공유","레시피","자유"};
        tabLayout = view.findViewById(R.id.tl_home);
        new TabLayoutMediator(tabLayout, viewpager,
                (tab, position) -> tab.setText(names[position])
        ).attach();
    }

//    public void filterButtonClickListener(){
//        btn_filter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavHostFragment navHostFragment = (NavHostFragment) ((MainActivity) MainActivity.mainContext)
//                        .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//                List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments().get(0)
//                        .getChildFragmentManager().getFragments();
//                ((ExchangeAndShare) fragments.get(0)).distanceSorting();
//                ((RecipeShare)fragments.get(1)).distanceSorting();
//                ((FreeCommunity)fragments.get(2)).distanceSorting();
//            }
//        });
//    }

}
