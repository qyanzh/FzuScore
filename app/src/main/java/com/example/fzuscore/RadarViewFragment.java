package com.example.fzuscore;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rorbin.q.radarview.RadarData;
import rorbin.q.radarview.RadarView;

import static android.content.Context.MODE_PRIVATE;

public class RadarViewFragment extends Fragment {

    List<String> mSubjectList = new ArrayList<>();
    List<Float > mScoreList = new ArrayList<>();
    SharedPreferences spf;

    public static RadarViewFragment newInstance(int term) {
        RadarViewFragment radarViewFragment = new RadarViewFragment();
        Bundle bundle = new Bundle();
        radarViewFragment.setTerm(term);
        radarViewFragment.setArguments(bundle);
        return radarViewFragment;
    }

    int term;

    public void setTerm(int term) {
        this.term = term;
    }

    public int getTerm() {
        return term;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.radar_chart, container, false);
        RadarChart radarChart = view.findViewById(R.id.radarChart);
        List<RadarEntry> entries = new ArrayList<>();

        initList();

        System.out.println(mScoreList.get(0));
        System.out.println(mScoreList.get(1));
        System.out.println(mScoreList.get(2));
        System.out.println(mScoreList.get(3));
        System.out.println(mScoreList.get(4));
        System.out.println(mScoreList.get(5));

        for (int i=0;i<6;i++){
            entries.add(new RadarEntry(i,mScoreList.get(i)));
        }

        Legend legend = radarChart.getLegend();
        legend.setXEntrySpace(2f);
        legend.setYEntrySpace(1f);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(12f);

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(6,false);
        yAxis.setTextSize(15f);
        yAxis.setDrawLabels(false);

        RadarDataSet set1 = new RadarDataSet(entries,"排名");
        set1.setDrawFilled(true);
        set1.setLineWidth(2f);

        radarChart.invalidate();

        return view;
    }

    private void initList() {
        spf = getActivity().getSharedPreferences("info",MODE_PRIVATE);
        String responseData = spf.getString("scoreJSON",null);
        parseJSON(responseData);
    }

    private void parseJSON(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                JSONArray subjectJSONArray = json.getJSONArray("subjects");
                for (int j = 0; j < subjectJSONArray.length(); j++) {
                    JSONObject subjectJSON = subjectJSONArray.getJSONObject(j);
                    String subject_name = subjectJSON.getString("subject_name");
                    double score = subjectJSON.getDouble("subject_score");
                    mSubjectList.add(subject_name);
                    mScoreList.add(Float.parseFloat(String.valueOf(score)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
