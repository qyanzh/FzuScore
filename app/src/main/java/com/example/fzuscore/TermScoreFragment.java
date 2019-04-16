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

public class TermScoreFragment extends Fragment implements Comparable<TermScoreFragment>{
    public static TermScoreFragment newInstance(List<Subject> subjectList,int term) {
        TermScoreFragment termScoreFragment = new TermScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("subjectList",(ArrayList<Subject>)subjectList);
        termScoreFragment.setTerm(term);
        termScoreFragment.setArguments(bundle);
        return termScoreFragment;
    }

    int term;

    public void setTerm(int term) {
        this.term = term;
    }

    public int getTerm() {
        return term;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        List<Subject> subjectList = getArguments().getParcelableArrayList("subjectList");
        RecyclerView recyclerView = view.findViewById(R.id.main_recyclerview);
        SubjectAdapter subjectAdapter;
        subjectAdapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(subjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    @Override
    public int compareTo(TermScoreFragment o) {
        return o.term - this.term;
    }

}
