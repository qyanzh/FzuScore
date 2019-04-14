package com.example.fzuscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;


import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    long lastBackTime;

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

        SharedPreferences spf =getSharedPreferences("info", MODE_PRIVATE);
        if(spf.getBoolean("logined",false)) {
            int amountOfTerms = spf.getInt("term_amount",0);
            String userName = spf.getString("user_name","用户名");
            int[] termList;
            try {
                JSONArray termsJSON = new JSONArray(spf.getString("termJSONArray",""));
                termList = JSONUtils.getIntArrayFromJSONArray(termsJSON);
                TabLayout tabLayout = findViewById(R.id.tabs);
                for (int i = amountOfTerms - 1; i >= 0; i--) {
                    tabLayout.addTab(tabLayout.newTab().setText(String.valueOf(termList[i])));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        test();
    }


    private void test() {
        RecyclerView recyclerView = findViewById(R.id.main_recyclerview);
        List<Subject> subjectList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            subjectList.add(new Subject("subject" + i, 90 + i, 60 + i));
        }
        SubjectAdapter subjectAdapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(subjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_overview:
                break;
            case R.id.nav_analysis:
                break;
            case R.id.nav_class_overview:
                break;
            case R.id.nav_forms:
                break;
            case R.id.nav_logout:
                quitAccount();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_exit:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void quitAccount() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            try {
                logout(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void logout(OkHttpClient client) throws IOException {
        String url = "http://47.112.10.160:3389/api/logout";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).execute();
        SharedPreferences.Editor spf = getSharedPreferences("info", MODE_PRIVATE).edit();
        spf.clear();
        spf.commit();
    }
}