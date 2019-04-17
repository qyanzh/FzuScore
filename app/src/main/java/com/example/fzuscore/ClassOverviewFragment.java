package com.example.fzuscore;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class ClassOverviewFragment extends Fragment {


    private List<SubjectForCard> subjectList = new ArrayList<>();
    private int term;

    public String getTitle() {
        return term + "";
    }

    public static ClassOverviewFragment newInstance(int term, List<SubjectForCard> list) {
        ClassOverviewFragment fragment = new ClassOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", (ArrayList<SubjectForCard>) list);
        args.putInt("term", term);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subjectList = getArguments().getParcelableArrayList("list");
        term = getArguments().getInt("term");
        System.out.println(term);
        System.out.println(subjectList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_overview, container, false);
        RecyclerView recyclerView;
        recyclerView = view.findViewById(R.id.recycler_view_class);
        SubjectForCardAdapter adapter = new SubjectForCardAdapter(subjectList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        System.out.println("list" + term + subjectList);
        return view;
    }

}
