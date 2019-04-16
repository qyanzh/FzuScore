package com.example.fzuscore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScoreRankActivity extends AppCompatActivity {

    private List<ScoreRankStudent> studentList = new ArrayList<>();
    private  String subjectName;
    private int term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_rank);

        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subject_name");
        term = intent.getIntExtra("term",0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(term==0){
            initSubjectRank();
            getSupportActionBar().setTitle(subjectName);
        }else{
            initTermRank();
            getSupportActionBar().setTitle("总成绩排行榜");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_rank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ScoreRankAdapter adapter = new ScoreRankAdapter(studentList);
        recyclerView.setAdapter(adapter);
    }

    private void initTermRank() {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("term",term);
                String responseData = RequestUtils.getJSON("rank_list",jsonObject,null);
                parseTermJSON(responseData);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
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
                System.out.println(name+" "+id+" "+score+" "+rank);
                studentList.add(new ScoreRankStudent(name,score,id,rank));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSubjectRank() {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("subject_name",subjectName);
                String responseData = RequestUtils.getJSON("subject",jsonObject,null);
                parseSubjectJSON(responseData);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
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
                System.out.println(name+" "+id+" "+score+" "+rank);
                studentList.add(new ScoreRankStudent(name,score,id,rank));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
