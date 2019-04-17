package com.example.fzuscore;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BottomDialogFragment extends BottomSheetDialogFragment {
    public static BottomDialogFragment newInstance(int perfect, int good, int pass, int die,int total) {
        Bundle args = new Bundle();
        args.putInt("perfect", perfect);
        args.putInt("good", good);
        args.putInt("pass", pass);
        args.putInt("die", die);
        args.putInt("total", total);
        BottomDialogFragment fragment = new BottomDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bottom_sheet, container);
        ViewPager viewPager = view.findViewById(R.id.chart_viewpager);
        TabLayout tabLayout = view.findViewById(R.id.chart_tab);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(PieChartFragment.newInstance((Bundle) getArguments().clone()));
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "分段统计" : "柱状图";
            }
        };
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int height = (point.y) / 2;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                height + 100);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        View view = dialog.getWindow().findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(view).setPeekHeight(height + 100);
    }
}
