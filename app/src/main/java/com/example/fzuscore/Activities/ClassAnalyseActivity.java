package com.example.fzuscore.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.fzuscore.Fragments.ProgressAnalyseFragment;
import com.example.fzuscore.Fragments.RadarChartFragment;
import com.example.fzuscore.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class ClassAnalyseActivity extends BaseActivity {

    List<Fragment> fragmentList = new ArrayList<>();
    List<String> tabTitleList = new ArrayList<>();
    int currentTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("班级分析");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment f1 = RadarChartFragment.newInstance(1);
        Fragment f2 = ProgressAnalyseFragment.newInstance();
        fragmentList.add(f1);
        fragmentList.add(f2);
        tabTitleList.add("优劣学科分析");
        tabTitleList.add("进步情况");
        initViewPager();
    }

    private void initViewPager() {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitleList.get(position);
            }
        };
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_analyse_change_term:
                return false;
            case R.id.action_toExcel_change:
                return false;
        }
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_change_term, menu);
//        return true;
//    }


}