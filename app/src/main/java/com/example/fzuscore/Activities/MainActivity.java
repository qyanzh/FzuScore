package com.example.fzuscore.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fzuscore.Adapters.TermScoreFragmentAdapter;
import com.example.fzuscore.DataBeans.Subject;
import com.example.fzuscore.DataBeans.UserInfo;
import com.example.fzuscore.Fragments.TermScoreFragment;
import com.example.fzuscore.R;
import com.example.fzuscore.Utils.Crypt;
import com.example.fzuscore.Utils.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    long lastBackTime;
    SharedPreferences spf;

    List<List<Subject>> termSubjectList = new ArrayList<>();
    List<TermScoreFragment> termScoreFragmentList = new ArrayList<>();

    List<Subject> mSubjectList = new ArrayList<>();
    private String excelFilePath = "";
    private String[] colNames = new String[]{"科目","排名", "成绩","平均分"};
    File path;

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


        path = Environment.getExternalStorageDirectory();
        System.out.println(path);
    }

    private void writeExcel(String path,List<Subject> subjects) {
        WritableWorkbook book = null;
        System.out.println(path);
        try{
            book = Workbook.createWorkbook(new File(path+"/"+UserInfo.getStudent_id_str()+" "+UserInfo.getUser_name()+".xls"));
            //生成名为eccif的工作表，参数0表示第一页
            WritableSheet sheet = book.createSheet("eccif", 0);
            for(int j=0;j<4;j++){
                Label label = new Label(j, 0, colNames[j]);
                sheet.addCell(label);
            }
            for(int i=0;i<subjects.size();i++){
                sheet.addCell(new Label(0,i+1,subjects.get(i).getName()));
                sheet.addCell(new Label(1,i+1,String.valueOf(subjects.get(i).getRank())));
                sheet.addCell(new Label(2,i+1,String.valueOf(subjects.get(i).getMyScore())));
                sheet.addCell(new Label(3,i+1,String.valueOf(subjects.get(i).getAvrScore())));
            }
            // 写入数据并关闭文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(book!=null){
                try {
                    book.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
        System.out.println("getFrom" + JSON);
        return JSON;
    }


    private void parseJSON(String responseData) {
        try {
            boolean isMonitor = UserInfo.isIsMonitor();
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
                    mSubjectList.add(new Subject(subject_name, subject_score, subject_averscore, subject_rank, subject_amount));
                }
                if (isMonitor) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("term", term);
                    String responseTermClassData = RequestUtils.getJSONByPost("rank_list", jsonObject, null);
                    System.out.println(responseTermClassData);
                    spf.edit().putString("classTotalScoreJSON" + term, responseTermClassData).apply();
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
                    RecyclerView recyclerView;
                    recyclerView = viewPager.findViewById(R.id.main_recyclerview);
                    if (recyclerView != null) {
                        runLayoutAnimation(recyclerView);
                    }
                    viewPager.getAdapter().notifyDataSetChanged();
                }
                break;
            case R.id.action_toExcel:
                try {
                    //检测是否有写的权限
                    int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                            "android.permission.WRITE_EXTERNAL_STORAGE");
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 没有写的权限，去申请写的权限，会弹出对话框
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                    } else {
                        writeExcel(path.toString(),mSubjectList);
                        Toast.makeText(MainActivity.this,"成功导出EXCEL到SD卡",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        drawer.closeDrawer(GravityCompat.START, false);


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
        if (len < 8 || len > 12) return false;
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
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_in_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
        super.onPause();
    }
}