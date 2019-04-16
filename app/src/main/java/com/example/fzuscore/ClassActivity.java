package com.example.fzuscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClassActivity extends AppCompatActivity {

    SharedPreferences spf;
    private ArrayList<Integer> termList = new ArrayList<>();
    private SubjectCardAdapter adapter;
    private TextView tvOptions;
    private int termOption;
    private int term;
    private Button btTermChange;
    private RecyclerView view;
    private List<List<SubjectForCard>> termSubjectList = new ArrayList<>();
    private List<SubjectForCard> currentList = new ArrayList<>();
    private Button btTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("班级概览");

        spf = getSharedPreferences("info",MODE_PRIVATE);
        String responseData = spf.getString("scoreJSON",null);
        System.out.println(responseData);
        System.out.print("**************************");
        parseJSON(responseData);

        view = findViewById(R.id.recycler_view_class);
        termOption = 0;
        term = 201802;
        //initSubject();

        initView();

        initPickerView();

        btTotal = findViewById(R.id.bt_total_list);
        btTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassActivity.this,ScoreRankActivity.class);
                intent.putExtra("term",term);
                startActivity(intent);
            }
        });
    }

    private void initPickerView() {
        getOptionitem();
        btTermChange = findViewById(R.id.bt_change_term);
        tvOptions = findViewById(R.id.tv_term_option);
        OptionsPickerView pvOptions = new OptionsPickerBuilder(ClassActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                termOption = termList.get(options1);
                if(termOption==201802) termOption = 0;
                else if(termOption==201801) termOption = 1;
                else if(termOption==201702) termOption = 2;
                else if(termOption==201701) termOption = 3;
                tvOptions.setText(String.valueOf(termList.get(options1)));
                term = termList.get(options1);
                currentList.clear();
                currentList.addAll(termSubjectList.get(termOption));
                view.getAdapter().notifyDataSetChanged();
            }
        }).setTitleText("选择学期")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0)//默认选中项
                .build();
        pvOptions.setPicker(termList);
        btTermChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });
    }

    private void getOptionitem() {
        termList.add(201802);
        termList.add(201801);
        termList.add(201702);
        termList.add(201701);
    }


    private void initView() {
        view = findViewById(R.id.recycler_view_class);
        view.setLayoutManager(new LinearLayoutManager(this));
        currentList.addAll(termSubjectList.get(termOption));
        adapter = new SubjectCardAdapter(currentList);
        view.setAdapter(adapter);
    }

    private void initSubject(){
        new Thread(()-> {
                try{
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .build();
                    String url = "http://47.112.10.160:3389/api/score";
                    JSONObject idJSON = new JSONObject();
                    idJSON.put("student_id", UserInfo.getStudent_id());
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), idJSON.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    System.out.print("*************************");
                    parseJSON(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }).start();
    }

    private void parseJSON(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                List<SubjectForCard> mSubjectList = new ArrayList<>();
                JSONObject json = jsonArray.getJSONObject(i);
                JSONArray subjectJSONArray = json.getJSONArray("subjects");
                int term = json.getInt("term");
                for (int j = 0; j < subjectJSONArray.length(); j++) {
                    JSONObject subjectJSON = subjectJSONArray.getJSONObject(j);
                    double subject_perfect = subjectJSON.getDouble("subject_perfect");
                    subject_perfect = subject_perfect * 100;
                    double subject_pass = subjectJSON.getDouble("subject_pass");
                    subject_pass = subject_pass * 100;
                    double subject_min = subjectJSON.getDouble("subject_min");
                    double subject_max = subjectJSON.getDouble("subject_max");
                    double subject_score = subjectJSON.getDouble("subject_score");
                    //int subject_rank = subjectJSON.getInt("subject_rank");
                    String subject_name = subjectJSON.getString("subject_name");
                    double subject_averscore = subjectJSON.getDouble("subject_averscore");
                    mSubjectList.add(new SubjectForCard(subject_name,term,subject_perfect,subject_pass, subject_averscore, subject_max,subject_min));
                }
                termSubjectList.add(mSubjectList);
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
