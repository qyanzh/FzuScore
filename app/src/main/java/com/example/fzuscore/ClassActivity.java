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

import java.util.ArrayList;
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

        initSubject();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectCardAdapter(mSubjectList);
        recyclerView.setAdapter(adapter);
    }
    private void initSubject(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    String url = "http://47.112.10.160:3389/api/score";
                    RequestScoreJSON requestScoreJSON = new RequestScoreJSON(31799101,201701);
                    String json = new Gson().toJson(requestScoreJSON);
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    System.out.print("**********");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
