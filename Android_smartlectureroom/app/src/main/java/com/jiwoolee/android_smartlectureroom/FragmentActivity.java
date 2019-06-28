package com.jiwoolee.android_smartlectureroom;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.jiwoolee.android_smartlectureroom.base.BaseActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class FragmentActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
    public void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new Fragment_First(), "추천");
        adapter.addFragment(new Fragment_Second(), "오늘의 추천");
        adapter.addFragment(new Fragment_Third(), "최근 등록");
        viewPager.setAdapter(adapter);
    }
}
