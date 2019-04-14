package com.example.fzuscore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScoreListActivity extends AppCompatActivity {

    private List<SubjectForCard> mSubjectList = new ArrayList<>();

    private SubjectCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("成绩汇总");
        initSubject();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectCardAdapter(mSubjectList);
        recyclerView.setAdapter(adapter);

    }


    private void initSubject(){
        for (int i=0;i<20;i++){
            SubjectForCard subject = new SubjectForCard("高等数学",i,i,i,i,i);
            mSubjectList.add(subject);
        }
    }

}
