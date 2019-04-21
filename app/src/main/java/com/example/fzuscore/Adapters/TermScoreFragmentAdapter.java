package com.example.fzuscore.Adapters;

import com.example.fzuscore.Fragments.TermScoreFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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