package com.example.fzuscore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ScoreRankActivity extends AppCompatActivity {

    private List<ScoreRankStudent> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_rank);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("成绩排名");
        initRank();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_rank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ScoreRankAdapter adapter = new ScoreRankAdapter(studentList);
        recyclerView.setAdapter(adapter);
    }

    private void initRank() {
        for (int i = 0; i < 50; i++) {
            ScoreRankStudent student = new ScoreRankStudent("name" + i, i + 500, i + 10000, i);
            studentList.add(student);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
