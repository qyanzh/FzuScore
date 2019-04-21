package com.example.fzuscore.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.fzuscore.Activities.ScoreRankActivity;
import com.bin.david.form.core.SmartTable;
import com.example.fzuscore.DataBeans.ScoreRankStudent;
import com.example.fzuscore.DataBeans.StudentTotalScore;

import com.example.fzuscore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ProgressAnalyseFragment extends Fragment {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private String[] colNames = new String[]{"学号", "姓名","总成绩","本期","上期","进步"};
    File path;

    public static ProgressAnalyseFragment newInstance() {
        ProgressAnalyseFragment fragment = new ProgressAnalyseFragment();
//        Bundle args = new Bundle();
//
//        fragment.setArguments(args);
        return fragment;
    }

    SharedPreferences spf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {

        }
        spf = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        parseJSON(spf.getString("scoreJSON", null));

        path = Environment.getExternalStorageDirectory();
    }


    Map<Integer, StudentTotalScore> termRankMap = new HashMap<>();

    private void parseJSON(String scoreJSON) {
        try {
            JSONArray jsonArray = new JSONArray(scoreJSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String term = jsonObject.getString("term");
                terms.add(term);
                String totalJSON = spf.getString("classTotalScoreJSON" + term, "");
                try {
                    JSONArray termRankArray = new JSONArray(totalJSON);
                    for (int j = 0; j < termRankArray.length(); j++) {
                        JSONObject subjectJSON = termRankArray.getJSONObject(j);
                        String name = subjectJSON.optString("student_name");
                        int id = subjectJSON.optInt("student_id");
                        double score = subjectJSON.optDouble("score");
                        int rank = subjectJSON.optInt("rank");
                        StudentTotalScore s = termRankMap.getOrDefault(id, new StudentTotalScore(id, name));
                        s.getScoreList().add(score);
                        s.getRankList().add(rank);
                        termRankMap.put(id, s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (StudentTotalScore value : termRankMap.values()) {
                value.calculateProgress();
            }
            list = new ArrayList<>(termRankMap.values());
            list.forEach(s -> s.onTermChanged(currentTermIndex));
            Collections.sort(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    List<StudentTotalScore> list;
    List<StudentTotalScore> mExcelList;
    SmartTable<StudentTotalScore> table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress_analyse, container, false);
        table = view.findViewById(R.id.table);
        table.setData(list);
        table.getConfig().setShowTableTitle(false);
        table.getConfig().setHorizontalPadding(20);
        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setMinTableWidth(getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth());
        return view;
    }

    List<String> terms = new ArrayList<>();
    int currentTermIndex;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_analyse_change_term:
                OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), (options1, option2, options3, v) -> {
                    //返回的分别是三个级别的选中位置
                    currentTermIndex = options1;
                    list.forEach(s -> s.onTermChanged(currentTermIndex));
                    Collections.sort(list);
                    table.setData(list);
                    System.out.println(list.get(0).getProgressList());
                })
                        .setSubmitText("确定")
                        .setCancelText("取消")
                        .setSelectOptions(currentTermIndex)
                        .build();
                List<String> subTerms = terms.subList(0, terms.size() - 1);
                pvOptions.setPicker(subTerms);
                pvOptions.show();
                break;
            case R.id.action_toExcel_change:
                System.out.println(2323);
                Toast.makeText(getActivity(), "]]]]]", Toast.LENGTH_SHORT).show();
                try {
                    //检测是否有写的权限
                    int permission = ActivityCompat.checkSelfPermission(getActivity(),
                            "android.permission.WRITE_EXTERNAL_STORAGE");
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 没有写的权限，去申请写的权限，会弹出对话框
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                    } else {
                        writeExcel(path.toString(),mExcelList);
                        Toast.makeText(getActivity(),"成功导出EXCEL到SD卡",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_term, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void writeExcel(String path,List<StudentTotalScore> subjects) {
        WritableWorkbook book = null;
        System.out.println(path);
        DecimalFormat df = new DecimalFormat("000000000");
        try{
                book = Workbook.createWorkbook(new File(path+"/"+terms.get(currentTermIndex)+"排名进步情况.xls"));
                WritableSheet sheet = book.createSheet("eccif", 0);
                for(int j=0;j<6;j++){
                    Label label = new Label(j, 0, colNames[j]);
                    sheet.addCell(label);
                }

                for(int i=0;i<subjects.size();i++){
                    sheet.addCell(new Label(0,i+1,df.format(subjects.get(i).getId())));
                    sheet.addCell(new Label(1,i+1,String.valueOf(subjects.get(i).getName())));
                    sheet.addCell(new Label(2,i+1,String.valueOf(subjects.get(i).getScoreList().get(currentTermIndex))));
                    sheet.addCell(new Label(3,i+1,String.valueOf(subjects.get(i).getRankList().get(currentTermIndex))));
                    sheet.addCell(new Label(4,i+1,String.valueOf(subjects.get(i).getRankList().get(currentTermIndex+1))));
                    sheet.addCell(new Label(5,i+1,String.valueOf(subjects.get(i).getProgressList().get(currentTermIndex))));
                }
                // 写入数据并关闭文件
                book.write();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}