package com.example.fzuscore.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.fzuscore.Fragments.TermScoreFragment;

import java.util.List;

public class TermScoreFragmentAdapter extends FragmentPagerAdapter {

    private List<TermScoreFragment> fragmentList;

    public TermScoreFragmentAdapter(FragmentManager fm, List<TermScoreFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

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
        return fragmentList.get(position).getTerm()+"";
    }
}