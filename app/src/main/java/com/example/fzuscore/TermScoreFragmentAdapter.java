package com.example.fzuscore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

class TermScoreFragmentAdapter extends FragmentPagerAdapter {

    private List<TermScoreFragment> fragmentList;
    private List<String> titleList;

    public TermScoreFragmentAdapter(FragmentManager fm, List<TermScoreFragment> fragmentList,List<String> titles) {
        super(fm);
        this.fragmentList=fragmentList;
        this.titleList = titles;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}