package com.example.fzuscore.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bin.david.form.core.SmartTable;
import com.example.fzuscore.DataBeans.StudentTotalScore;
import com.example.fzuscore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class ProgressAnalyseFragment extends Fragment {


    public static ProgressAnalyseFragment newInstance() {
        ProgressAnalyseFragment fragment = new ProgressAnalyseFragment();
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
    //    RecyclerView recyclerView;
//    ProgressRankAdapter adapter;
    SmartTable<StudentTotalScore> table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress_analyse, container, false);
//        recyclerView = view.findViewById(R.id.recycler_view_rank);
//        adapter = new ProgressRankAdapter(list, currentTermIndex);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
//                    table.notifyDataChanged();
//                    adapter.setCurrentIndex(currentTermIndex);
//                    adapter.notifyDataSetChanged();
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
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_term, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}