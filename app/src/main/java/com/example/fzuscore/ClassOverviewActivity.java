package com.example.fzuscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClassOverviewActivity extends AppCompatActivity {

    private SharedPreferences spf;
    List<Fragment> fragmentList = new ArrayList<>();
    List<List<SubjectForCard>> termSubjectList = new ArrayList<>();
    List<Integer> termList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("班级概览");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spf = getSharedPreferences("info", MODE_PRIVATE);

        String responseData = spf.getString("scoreJSON", null);
        parseJSON(responseData);
        System.out.println("tttt" + responseData);
        for (int i = 0; i < termList.size(); i++) {
            Fragment f = ClassOverviewFragment.newInstance(termList.get(i), termSubjectList.get(i));
            fragmentList.add(f);
        }
        initViewPager();
    }


    private void initViewPager() {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return termList.get(position) + "";
            }
        };
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void parseJSON(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                List<SubjectForCard> mSubjectList = new ArrayList<>();
                JSONObject json = jsonArray.getJSONObject(i);
                int term = json.getInt("term");
                JSONArray subjectJSONArray = json.getJSONArray("subjects");
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
                    mSubjectList.add(new SubjectForCard(subject_name, term, subject_perfect, subject_pass, subject_averscore, subject_max, subject_min));
                }
                termList.add(term);
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
            case R.id.menu_class_term_total:
                Intent intent = new Intent(ClassOverviewActivity.this, ScoreRankActivity.class);
                ViewPager viewPager = findViewById(R.id.viewpager);
                intent.putExtra("term", termList.get(viewPager.getCurrentItem()));
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_class_overview, menu);
        return true;
    }

}
