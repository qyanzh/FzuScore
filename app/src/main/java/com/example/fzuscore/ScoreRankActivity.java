package com.example.fzuscore;

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
        new Thread(() -> {
            try {
                totalData();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    private synchronized void totalData() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "http://47.112.10.160:3389/api/score";
        RequestScoreJSON requestScoreJSON = new RequestScoreJSON(UserInfo.getStudent_id(), 201701);
        String json = new Gson().toJson(requestScoreJSON);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        System.out.println(responseData);
        parseJSON(responseData);
    }

    private void parseJSON(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("subjects");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subjectJSON = jsonArray.getJSONObject(i);
                double subject_min = subjectJSON.optDouble("subject_min");
                double subject_max = subjectJSON.optDouble("subject_max");
                //int subject_rank = subjectJSON.getInt("subject_rank");
                String subject_name = subjectJSON.optString("subject_name");
                double subject_averscore = subjectJSON.optDouble("subject_averscore");
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
