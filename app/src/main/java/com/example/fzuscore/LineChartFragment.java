package com.example.fzuscore;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class LineChartFragment extends Fragment {

    private SharedPreferences spf;
    private List<String> termList = new ArrayList<>();
    private List<Integer> rankList = new ArrayList<>();

    public static LineChartFragment newInstance() {
        LineChartFragment lineChartFragment = new LineChartFragment();
        return lineChartFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.line_chart, container, false);

        LineChart chart = view.findViewById(R.id.lineChart);
        ArrayList<Entry> entries = new ArrayList<>();
        spf = getActivity().getSharedPreferences("info", MODE_PRIVATE);
        String responseData = spf.getString("scoreJSON", null);
        parseJSON(responseData);
        System.out.println(responseData);
        System.out.println(termList.get(0) + " " + rankList.get(0));
        System.out.println(termList.get(1) + " " + rankList.get(1));
        System.out.println(termList.get(2) + " " + rankList.get(2));
        System.out.println(termList.get(3) + " " + rankList.get(3));

        for (int i = 0; i < 4; i++) {
            entries.add(new Entry(i, rankList.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "成绩");
        dataSet.setColor(Color.parseColor("#7d7d7d"));
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextSize(12f);
        dataSet.setCircleColor(Color.parseColor("#7d7d7d"));
        dataSet.setLineWidth(1f);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(15f);
        leftAxis.setInverted(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setSpaceMin(1f);
        xAxis.setSpaceMax(1f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(4);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(termList));

        Description description = new Description();
        description.setEnabled(false);
        chart.setDescription(description);

        chart.invalidate();

        return view;
    }

    private void parseJSON(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                JSONObject json = jsonArray.getJSONObject(i);
                int term = json.getInt("term");
                int rank = json.getInt("rank");
                rankList.add(rank);
                termList.add(String.valueOf(term));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
