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

import java.util.ArrayList;
import java.util.List;

public class TermScoreFragment extends Fragment {
    public static TermScoreFragment newInstance(List<Subject> subjectList) {
        TermScoreFragment termScoreFragment = new TermScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("subjectList",(ArrayList<Subject>)subjectList);
        termScoreFragment.setArguments(bundle);
        return termScoreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        List<Subject> list = getArguments().getParcelableArrayList("subjectList");
        RecyclerView recyclerView = view.findViewById(R.id.main_recyclerview);
        SubjectAdapter subjectAdapter = new SubjectAdapter(list);
        recyclerView.setAdapter(subjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
}
