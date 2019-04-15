package com.example.fzuscore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RequestUtils.ResponseListener {

    long lastBackTime;
    SharedPreferences spf;

    List<List<Subject>> termSubjectList = new ArrayList<>();
    List<TermScoreFragment> termScoreFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spf = getSharedPreferences("info", MODE_PRIVATE);
        initViewPager();
        initInfo();
    }

    private void initViewPager() {
        TermScoreFragmentAdapter adapter = new TermScoreFragmentAdapter(getSupportFragmentManager(), termScoreFragmentList);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initInfo() {
        String userName = spf.getString("user_name", "用户名");
        String userIdStr = spf.getString("user_account", "学号");
        UserInfo.setInfo(userIdStr, userName);
        String JSON = spf.getString("scoreJSON", "");
        if (!isWebConnect()) {
            Snackbar.make(findViewById(android.R.id.content), "网络不可用", Snackbar.LENGTH_SHORT).show();
        } else {
            if (JSON.contentEquals("")) {
                JSON = getJSONFromServer();
            };
            parseJSON(JSON);
        }
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    private String getJSONFromServer() {
        String JSON;
        JSONObject idJSON = new JSONObject();
        try {
            idJSON.put("student_id", UserInfo.getStudent_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSON = RequestUtils.getJSON("score", idJSON, this);
        spf.edit().putString("scoreJSON", JSON).apply();
        System.out.println("getFrom");
        return JSON;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean isWebConnect() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    private void parseJSON(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                List<Subject> subjectList = new ArrayList<>();
                JSONObject json = jsonArray.getJSONObject(i);
                JSONArray subjectJSONArray = json.getJSONArray("subjects");
                int term = json.getInt("term");
                for (int j = 0; j < subjectJSONArray.length(); j++) {
                    JSONObject subjectJSON = subjectJSONArray.getJSONObject(j);
                    double subject_score = subjectJSON.getDouble("subject_score");
                    int subject_rank = subjectJSON.getInt("subject_rank");
                    String subject_name = subjectJSON.getString("subject_name");
                    double subject_averscore = subjectJSON.getDouble("subject_averscore");
                    subjectList.add(new Subject(subject_name, subject_score, subject_averscore, subject_rank));
                }
                termSubjectList.add(subjectList);
                termScoreFragmentList.add(TermScoreFragment.newInstance(subjectList, term));
                Collections.sort(termScoreFragmentList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (!isWebConnect()) {
                    Snackbar.make(findViewById(android.R.id.content), "网络不可用", Snackbar.LENGTH_SHORT).show();
                } else {
                    termSubjectList.clear();
                    termScoreFragmentList.clear();
                    String JSON = getJSONFromServer();
                    spf.edit().putString("scoreJSON", JSON).apply();
                    parseJSON(JSON);
                    ViewPager viewPager = findViewById(R.id.viewpager);
                    viewPager.getAdapter().notifyDataSetChanged();
                }
                break;
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_overview:
                break;
            case R.id.nav_analysis:
                break;
            case R.id.nav_class_overview:
                intent = new Intent(this, ClassActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_forms:
                intent = new Intent(this, ScoreListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                quitAccount();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_exit:
                finish();
                return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long thisTime = Calendar.getInstance().getTimeInMillis();
            if (thisTime - lastBackTime > 1000) {
                lastBackTime = thisTime;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

    private void quitAccount() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(8, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(8, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(9, TimeUnit.SECONDS)//设置连接超时时间
                    .build();
            try {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                editor.commit();
                String url = "http://47.112.10.160:3389/api/logout";
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onResponseSuccess() {
        Snackbar.make(findViewById(android.R.id.content), "刷新成功", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onResponseFailed() {
        Snackbar.make(findViewById(android.R.id.content), "服务器获取数据异常,请重试", Snackbar.LENGTH_SHORT).setAction("重试", v -> getJSONFromServer()).show();
    }
}