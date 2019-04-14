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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClassActivity extends AppCompatActivity {

    private List<SubjectForCard> mSubjectList = new ArrayList<>();

    private SubjectCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initSubject();
        initView();
    }

    private synchronized void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectCardAdapter(mSubjectList);
        recyclerView.setAdapter(adapter);
    }

    private void initSubject(){
        //mSubjectList.add(new SubjectForCard("高等数学",84.32, 90.43, 87.6,99,54));
        new Thread(()-> {
                try{
                    updateData();
                }catch (Exception e){
                    e.printStackTrace();
                }

        }).start();
    }

    private synchronized void updateData() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "http://47.112.10.160:3389/api/score";
        RequestScoreJSON requestScoreJSON = new RequestScoreJSON(UserInfo.getStudent_id(),201701);
        String json = new Gson().toJson(requestScoreJSON);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
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
                mSubjectList.add(new SubjectForCard(subject_name,69.3, 86.4, subject_averscore,subject_max,subject_min));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
