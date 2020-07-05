package com.hankki.fooddeal.ui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ux.viewpager.viewPagerAdapter;


/**소비자용, 사업자용 2개 탭뷰 구성*/

/**로그인 액티비티
 * 로그인 유지 상태일 경우, 이 화면은 건너뜀.
 * 로그인 유지 정보는 앱 내에서 기억해야 하므로 Preference Manager 이용할 듯.*/
public class LoginActivity extends AppCompatActivity {
    com.hankki.fooddeal.ux.viewpager.viewPagerAdapter viewPagerAdapter;
    ViewPager2 viewpager;
    Fragment[] fragments = new Fragment[2];
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setFragments();
        setViewPager();
        setTabLayout();
    }

    /**탭으로 구성할 Fragments 리스트*/
    public void setFragments(){
        fragments[0] = new ConsumerFragment();
        fragments[1] = new ProducerFragment();
    }

    /**View Pager -> ux.viewpager.viewPagerAdapter class*/
    public void setViewPager(){
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Lifecycle lifecycle = this.getLifecycle();
        viewPagerAdapter = new viewPagerAdapter(fragmentManager,lifecycle,fragments);
        viewpager = findViewById(R.id.vp_login);
        viewpager.setAdapter(viewPagerAdapter);
    }

    /**상단 탭 바에 나타낼 Title 적용*/
    public void setTabLayout(){
        String[] names = new String[]{"개인","사업자"};
        tabLayout = findViewById(R.id.tl_login);
        new TabLayoutMediator(tabLayout, viewpager,
                (tab, position) -> tab.setText(names[position])
        ).attach();
    }
}
