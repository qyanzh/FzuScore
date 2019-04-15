package com.example.fzuscore;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

    private List<SubjectForCard> mSubjectList = new ArrayList<>();

    private ArrayList<Integer> termList = new ArrayList<>();
    private SubjectCardAdapter adapter;
    private TextView tvOptions;
    private int termOption;
    private Button btTermChange;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        termOption = 201701;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("班级概览");
        long requestFrom = Calendar.getInstance().getTimeInMillis();
        initSubject();
//        while(mSubjectList.size() < 6 ){
//            if(Calendar.getInstance().getTimeInMillis() - requestFrom > 1000) {
//                break;
//            }
//        }
        initView();
        //initPickerView();

    }

    private void initPickerView() {
        getOptionitem();
        btTermChange = findViewById(R.id.bt_change_term);
        tvOptions = findViewById(R.id.tv_term_option);
        OptionsPickerView pvOptions = new OptionsPickerBuilder(ClassActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                termOption = termList.get(options1);
                tvOptions.setText(String.valueOf(termOption));
                viewPager.getAdapter().notifyDataSetChanged();
            }
        }).setTitleText("选择学期")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(3)//默认选中项
                .build();
        pvOptions.setPicker(termList);
        pvOptions.show();
        btTermChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });
    }

    private void getOptionitem() {
        termList.add(201701);
        termList.add(201702);
        termList.add(201801);
        termList.add(201802);
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectCardAdapter(mSubjectList);
        recyclerView.setAdapter(adapter);
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
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("subjects");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subjectJSON = jsonArray.getJSONObject(i);
                double subject_min = subjectJSON.optDouble("subject_min");
                double subject_max = subjectJSON.optDouble("subject_max");
                //int subject_rank = subjectJSON.getInt("subject_rank");
                String subject_name = subjectJSON.optString("subject_name");
                double subject_averscore = subjectJSON.optDouble("subject_averscore");
                double subject_perfect = subjectJSON.optDouble("subject_perfect");
                double subject_pass = subjectJSON.optDouble("subject_pass");
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
