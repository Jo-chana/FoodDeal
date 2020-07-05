package com.hankki.fooddeal.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ui.home.community.ExchangeAndShare;
import com.hankki.fooddeal.ui.home.community.FoodMate;
import com.hankki.fooddeal.ui.home.community.FreeCommunity;
import com.hankki.fooddeal.ui.home.community.RecipeShare;
import com.hankki.fooddeal.ux.viewpager.viewPagerAdapter;

/**홈 화면*/
public class HomeFragment extends Fragment {
    ViewPager2 viewpager;
    Fragment[] fragments = new Fragment[4];
    TabLayout tabLayout;
    viewPagerAdapter viewPagerAdapter;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_home, container, false);
        setFragments();
        setViewPager();
        setTabLayout();
        return view;
    }

    /**탭으로 구성할 Fragments 리스트*/
    public void setFragments(){
        fragments[0] = new ExchangeAndShare();
        fragments[1] = new FoodMate();
        fragments[2] = new RecipeShare();
        fragments[3] = new FreeCommunity();
    }

    /**View Pager -> ux.viewpager.viewPagerAdapter class*/
    public void setViewPager(){
        viewPagerAdapter = new viewPagerAdapter(this,fragments);
        viewpager = view.findViewById(R.id.vp_home);
        viewpager.setAdapter(viewPagerAdapter);
    }

    /**상단 탭 바에 나타낼 Title 적용*/
    public void setTabLayout(){
        /**이부분은 따로 String Class 정의해서 사용하는 것이 나을 듯*/
        String[] names = new String[]{"교환/나눔","밥친구","레시피","자유"};
        tabLayout = view.findViewById(R.id.tl_home);
        new TabLayoutMediator(tabLayout, viewpager,
                (tab, position) -> tab.setText(names[position])
        ).attach();
    }
}