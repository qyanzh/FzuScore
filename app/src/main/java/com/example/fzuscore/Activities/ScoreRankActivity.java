package com.example.fzuscore.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.example.fzuscore.DataBeans.ScoreRankStudent;
import com.example.fzuscore.Fragments.BottomDialogFragment;
import com.example.fzuscore.R;
import com.example.fzuscore.Utils.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ScoreRankActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private List<ScoreRankStudent> studentList = new ArrayList<>();
    private String subjectName;
    private int term;
    private String[] colNames = new String[]{"排名","学号", "姓名","成绩"};
    File path;
    List<ScoreRankStudent> mSubjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_rank);

        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subject_name");
        term = intent.getIntExtra("term", 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (term == 0) {
            initSubjectRank();
            getSupportActionBar().setTitle(subjectName);
        } else {
            initTermRank();
            getSupportActionBar().setTitle(term + "学期总成绩");
        }

        SmartTable<ScoreRankStudent> table = findViewById(R.id.table);
        table.setData(studentList);
        table.getConfig().setShowTableTitle(false);
        table.getConfig().setHorizontalPadding(20);
        table.getConfig().setMinTableWidth(getWindow().getWindowManager().getDefaultDisplay().getWidth());

        path = Environment.getExternalStorageDirectory();
    }

    private void initTermRank() {
        try {
            SharedPreferences spf = getSharedPreferences("info", MODE_PRIVATE);
            String responseData = spf.getString("classTotalScoreJSON" + term, "");
            parseTermJSON(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseTermJSON(String responseData) {
        try {
            System.out.println(responseData);
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subjectJSON = jsonArray.getJSONObject(i);
                String name = subjectJSON.optString("student_name");
                int id = subjectJSON.optInt("student_id");
                double score = subjectJSON.optDouble("score");
                int rank = subjectJSON.optInt("rank");
                studentList.add(new ScoreRankStudent(name, score, id, rank));
                mSubjectList.add(new ScoreRankStudent(name,score,id,rank));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSubjectRank() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subject_name", subjectName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String responseData = RequestUtils.getJSONByPost("subject", jsonObject, null);
        parseSubjectJSON(responseData);

    }

    private void parseSubjectJSON(String responseData) {
        try {
            System.out.println(responseData);
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("students");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subjectJSON = jsonArray.getJSONObject(i);
                String name = subjectJSON.optString("student_name");
                int id = subjectJSON.optInt("student_id");
                double score = subjectJSON.optDouble("subject_score");
                int rank = subjectJSON.optInt("rank");
                System.out.println(name + " " + id + " " + score + " " + rank);
                studentList.add(new ScoreRankStudent(name, score, id, rank));
                mSubjectList.add(new ScoreRankStudent(name,score,id,rank));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_score_rank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_rank_statics:
                int perfect=0,good=0,pass=0,die=0;
                for (ScoreRankStudent student : studentList) {
                    if(student.getScore()>=90) {
                        perfect++;
                    } else if (student.getScore() >= 78) {
                        good++;
                    } else if (student.getScore() >= 60) {
                        pass++;
                    } else {
                        die++;
                    }
                }
                BottomDialogFragment.newInstance(perfect,good,pass,die,studentList.size()).show(getSupportFragmentManager(),"tag");
                break;
            case R.id.action_toExcel_rank:
                try {
                    //检测是否有写的权限
                    int permission = ActivityCompat.checkSelfPermission(ScoreRankActivity.this,
                            "android.permission.WRITE_EXTERNAL_STORAGE");
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 没有写的权限，去申请写的权限，会弹出对话框
                        ActivityCompat.requestPermissions(ScoreRankActivity.this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                    } else {
                        writeExcel(path.toString(),mSubjectList);
                        Toast.makeText(ScoreRankActivity.this,"成功导出EXCEL到SD卡",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    private void writeExcel(String path,List<ScoreRankStudent> subjects) {
        WritableWorkbook book = null;
        System.out.println(path);
        DecimalFormat df = new DecimalFormat("000000000");
        try{
            if (term==0){
                book = Workbook.createWorkbook(new File(path+"/"+ subjectName+"排名情况.xls"));
                WritableSheet sheet = book.createSheet("eccif", 0);
                for(int j=0;j<4;j++){
                    Label label = new Label(j, 0, colNames[j]);
                    sheet.addCell(label);
                }
                for(int i=0;i<subjects.size();i++){
                    sheet.addCell(new Label(0,i+1,String.valueOf(subjects.get(i).getRank())));
                    sheet.addCell(new Label(1,i+1,df.format(subjects.get(i).getNumber())));
                    sheet.addCell(new Label(2,i+1,String.valueOf(subjects.get(i).getName())));
                    sheet.addCell(new Label(3,i+1,String.valueOf(subjects.get(i).getScore())));
                }
                // 写入数据并关闭文件
                book.write();
            }else{
                book = Workbook.createWorkbook(new File(path+"/"+term+"学期总排名情况.xls"));
                WritableSheet sheet = book.createSheet("eccif", 0);
                for(int j=0;j<4;j++){
                    Label label = new Label(j, 0, colNames[j]);
                    sheet.addCell(label);
                }
                for(int i=0;i<subjects.size();i++){
                    sheet.addCell(new Label(0,i+1,String.valueOf(subjects.get(i).getRank())));
                    sheet.addCell(new Label(1,i+1,df.format(subjects.get(i).getNumber())));
                    sheet.addCell(new Label(2,i+1,String.valueOf(subjects.get(i).getName())));
                    sheet.addCell(new Label(3,i+1,String.valueOf(subjects.get(i).getScore())));
                }
                // 写入数据并关闭文件
                book.write();
            }
            //生成名为eccif的工作表，参数0表示第一页

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
}
