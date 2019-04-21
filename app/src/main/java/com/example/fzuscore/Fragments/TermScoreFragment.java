package com.example.fzuscore.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.fzuscore.Adapters.SubjectAdapter;
import com.example.fzuscore.DataBeans.Subject;
import com.example.fzuscore.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TermScoreFragment extends Fragment {
    public static TermScoreFragment newInstance(List<Subject> subjectList, int term) {
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

    List<Subject> subjectList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subjectList = getArguments().getParcelableArrayList("subjectList");
    }

    RecyclerView recyclerView;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
       recyclerView = view.findViewById(R.id.main_recyclerview);

        LayoutAnimationController loadLayoutAnimation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_slide_in_bottom);
        recyclerView.setLayoutAnimation(loadLayoutAnimation);


        SubjectAdapter subjectAdapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(subjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }


}
