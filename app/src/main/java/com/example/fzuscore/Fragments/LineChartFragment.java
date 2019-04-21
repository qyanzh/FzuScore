package com.example.fzuscore.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fzuscore.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        chart.setTouchEnabled(false);
        ArrayList<Entry> entries = new ArrayList<>();
        spf = getActivity().getSharedPreferences("info", MODE_PRIVATE);
        String responseData = spf.getString("scoreJSON", null);
        parseJSON(responseData);

        for (int i = 0; i < termList.size(); i++) {
            entries.add(new Entry(i, rankList.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "成绩");
        int pink = getResources().getColor(R.color.colorAccent, null);
        dataSet.setColor(pink);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextSize(12f);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(1f);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(15f);
        leftAxis.setInverted(true);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(15f);
        xAxis.setSpaceMin(1f);
        xAxis.setSpaceMax(1f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(4);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(termList));
        xAxis.setYOffset(-1f);

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
