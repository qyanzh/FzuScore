package com.example.fzuscore;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Class extends AppCompatActivity {

    private List<SubjectForCard> mSubjectList = new ArrayList<>();

    private SubjectCradAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        initSubject();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectCradAdapter(mSubjectList);
        recyclerView.setAdapter(adapter);
    }
    private void initSubject(){
        for (int i=0;i<20;i++){
            SubjectForCard subject = new SubjectForCard("高等数学",i,i,i,i,i);
            mSubjectList.add(subject);
        }
    }
}
