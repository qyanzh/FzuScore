package com.example.fzuscore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TermScoreFragment extends Fragment {
    public static TermScoreFragment newInstance(List<Subject> subjectList,List<String> titles) {
        TermScoreFragment termScoreFragment = new TermScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("subjectList",(ArrayList<Subject>)subjectList);;
        bundle.putStringArrayList("titleList",(ArrayList<String>)titles);
        termScoreFragment.setArguments(bundle);
        return termScoreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        List<Subject> subjectList = getArguments().getParcelableArrayList("subjectList");
        List<String> titleList = getArguments().getStringArrayList("titleList");
        RecyclerView recyclerView = view.findViewById(R.id.main_recyclerview);
        subjectAdapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(subjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    SubjectAdapter subjectAdapter;
    public void refreshList() {
        subjectAdapter.notifyDataSetChanged();
    }
}
