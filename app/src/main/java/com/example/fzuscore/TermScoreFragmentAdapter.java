package com.example.fzuscore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

class TermScoreFragmentAdapter extends FragmentPagerAdapter {

    private List<TermScoreFragment> fragmentList;

    public TermScoreFragmentAdapter(FragmentManager fm, List<TermScoreFragment> fragmentList) {
        super(fm);
        this.fragmentList=fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}