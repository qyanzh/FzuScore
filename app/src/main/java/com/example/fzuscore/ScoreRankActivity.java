package com.example.fzuscore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScoreRankActivity extends AppCompatActivity {

    private List<ScoreRankStudent> studentList = new ArrayList<>();
    private String subjectName;
    private int term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_rank);

        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subject_name");
        term = intent.getIntExtra("term", 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (term == 0) {
            initSubjectRank();
            getSupportActionBar().setTitle(subjectName);
        } else {
            initTermRank();
            getSupportActionBar().setTitle(term + "学期总成绩");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_rank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ScoreRankAdapter adapter = new ScoreRankAdapter(studentList);
        recyclerView.setAdapter(adapter);
    }

    private void initTermRank() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("term", term);
            String responseData = RequestUtils.getJSONByPost("rank_list", jsonObject, null);
            parseTermJSON(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseTermJSON(String responseData) {
        try {
            System.out.println(responseData);
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subjectJSON = jsonArray.getJSONObject(i);
                String name = subjectJSON.optString("student_name");
                //double subject_averscore = subjectJSON.optDouble("subject_averscore");
                int id = subjectJSON.optInt("student_id");
                double score = subjectJSON.optDouble("score");
                int rank = subjectJSON.optInt("rank");
                studentList.add(new ScoreRankStudent(name, score, id, rank));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSubjectRank() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subject_name", subjectName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String responseData = RequestUtils.getJSONByPost("subject", jsonObject, null);
        parseSubjectJSON(responseData);

    }

    private void parseSubjectJSON(String responseData) {
        try {
            System.out.println(responseData);
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("students");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subjectJSON = jsonArray.getJSONObject(i);
                String name = subjectJSON.optString("student_name");
                //double subject_averscore = subjectJSON.optDouble("subject_averscore");
                int id = subjectJSON.optInt("student_id");
                double score = subjectJSON.optDouble("subject_score");
                int rank = subjectJSON.optInt("rank");
                System.out.println(name + " " + id + " " + score + " " + rank);
                studentList.add(new ScoreRankStudent(name, score, id, rank));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_score_rank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_rank_statics:
                int perfect=0,good=0,pass=0,die=0;
                for (ScoreRankStudent student : studentList) {
                    if(student.getScore()>=90) {
                        perfect++;
                    } else if (student.getScore() >= 78) {
                        good++;
                    } else if (student.getScore() >= 60) {
                        pass++;
                    } else {
                        die++;
                    }
                }
                BottomDialogFragment.newInstance(perfect,good,pass,die,studentList.size()).show(getSupportFragmentManager(),"tag");
                break;
        }
        return true;
    }
}
