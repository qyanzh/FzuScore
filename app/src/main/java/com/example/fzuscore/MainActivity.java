package com.example.fzuscore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        getSupportActionBar().setTitle("我的成绩");

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

        View headerView = navigationView.getHeaderView(0);
        TextView textUsername = headerView.findViewById(R.id.user_name);
        textUsername.setText(UserInfo.getUser_name());
        TextView textUserId = headerView.findViewById(R.id.user_account);
        textUserId.setText(UserInfo.getStudent_id_str());
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
        boolean isMonitor = spf.getBoolean("isMonitor", false);
        UserInfo.setInfo(userIdStr, userName, isMonitor);
        String JSON = spf.getString("scoreJSON", "");
        if (isMonitor) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.nav_monitor).setVisible(true);
        }
        if (!RequestUtils.isWebConnect(this)) {
            Snackbar.make(findViewById(android.R.id.content), "网络不可用", Snackbar.LENGTH_SHORT).show();
        } else {
            if (JSON.contentEquals("")) {
                JSON = getJSONFromServer();
            }
            parseJSON(JSON);
        }
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
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
        JSON = RequestUtils.getJSONByPost("score", idJSON, new RequestUtils.ResponseListener() {
            @Override
            public void onResponseSuccess() {
                Snackbar.make(findViewById(android.R.id.content), "刷新成功", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onResponseFailed() {
                Snackbar.make(findViewById(android.R.id.content), "服务器获取数据异常,请重试", Snackbar.LENGTH_SHORT).setAction("重试", v -> getJSONFromServer()).show();
            }
        });
        spf.edit().putString("scoreJSON", JSON).apply();
        System.out.println("getFrom");
        return JSON;
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
                    int subject_amount = subjectJSON.getInt("subject_amount");
                    subjectList.add(new Subject(subject_name, subject_score, subject_averscore, subject_rank, subject_amount));
                }
                termSubjectList.add(subjectList);
                termScoreFragmentList.add(TermScoreFragment.newInstance(subjectList, term));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (!RequestUtils.isWebConnect(this)) {
                    Snackbar.make(findViewById(android.R.id.content), "网络不可用", Snackbar.LENGTH_SHORT).show();
                } else {
                    termSubjectList.clear();
                    termScoreFragmentList.clear();
                    String JSON = getJSONFromServer();
                    spf.edit().putString("scoreJSON", JSON).apply();
                    parseJSON(JSON);
                    ViewPager viewPager = findViewById(R.id.viewpager);
                    RecyclerView recyclerView ;
                    recyclerView = viewPager.findViewById(R.id.main_recyclerview);
                    runLayoutAnimation(recyclerView);
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
                intent = new Intent(this, AnalyseActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_class_overview:
                intent = new Intent(this, ClassOverviewActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_class_analyse:
                intent = new Intent(this, ClassAnalyseActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_changePassword:
                if (RequestUtils.isWebConnect(this)) {
                    changePassword();
                } else {
                    Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_logout:
                if (RequestUtils.isWebConnect(this)) {
                    quitAccount();
                    return true;
                } else {
                    Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.layout_change_password, null);
        builder.setView(dialogView);
        EditText originPassword = dialogView.findViewById(R.id.editText_origin_password);
        EditText newPassword = dialogView.findViewById(R.id.editText_new_password);
        EditText confirmPassword = dialogView.findViewById(R.id.editText_confirm_password);
        builder.setTitle("修改密码");
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("显示/隐藏密码", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newPass = newPassword.getText().toString();
            String cofPass = confirmPassword.getText().toString();
            if (isLegal(newPass)) {
                if (newPass.equals(cofPass)) {
                    JSONObject requestJSON = new JSONObject();
                    try {
                        requestJSON.put("student_id", UserInfo.getStudent_id());
                        Crypt crypt = new Crypt();
                        requestJSON.put("password_old", crypt.encrypt_string(originPassword.getText().toString()));
                        requestJSON.put("password_new", crypt.encrypt_string(newPass));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    StringBuilder responseJSON = new StringBuilder();
                    responseJSON.append(RequestUtils.getJSONByPost("change_password", requestJSON, new RequestUtils.ResponseListener() {
                        @Override
                        public void onResponseSuccess() {
                        }

                        @Override
                        public void onResponseFailed() {
                            Toast.makeText(MainActivity.this, "服务器异常.请重试", Toast.LENGTH_SHORT).show();
                        }
                    }));
                    try {
                        JSONObject JSON = new JSONObject(responseJSON.toString());
                        Toast.makeText(this, JSON.getString("message"), Toast.LENGTH_SHORT).show();
                        if (JSON.getInt("is_success") == 1) {
                            quitAccount();
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("不一致");
                    Toast.makeText(this, "两次新密码输入不一致,请检查", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "密码只能由数字/字母/小数点/下划线构成,长度8-12位", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(view -> {
            if (!isChecked) {
                originPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                originPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            isChecked = !isChecked;
        });
    }

    private boolean isLegal(String newPass) {
        int len = newPass.length();
        if (len <= 8 || len > 12) return false;
        for (int i = 0; i < len; i++) {
            char c = newPass.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '.')
                return false;
        }
        return true;
    }

    boolean isChecked = false;

    private void quitAccount() {
        try {
            SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.clear().apply();
            RequestUtils.getJSONByGet("logout", null);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void runLayoutAnimation(RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_slide_in_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}