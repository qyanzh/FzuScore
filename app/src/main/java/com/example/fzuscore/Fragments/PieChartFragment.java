package com.example.fzuscore.Fragments;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fzuscore.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class PieChartFragment extends Fragment {
    public static PieChartFragment newInstance(Bundle dataBundle) {
        PieChartFragment fragment = new PieChartFragment();
        fragment.setArguments(dataBundle);
        return fragment;
    }

    int perfect, good, pass, die, total;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        perfect = data.getInt("perfect");
        good = data.getInt("good");
        pass = data.getInt("pass");
        die = data.getInt("die");
        total = data.getInt("total");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

//        DecimalFormat df = new DecimalFormat("0.00%");

        List<PieEntry> entries = new ArrayList<>();
        if (perfect > 0) {
            entries.add(new PieEntry((float) perfect / total, "优秀 " + perfect + "人"));
        }
        if (good > 0) {
            entries.add(new PieEntry((float) good / total, "良好 " + good + "人"));
        }
        if (pass > 0) {
            entries.add(new PieEntry((float) pass / total, "及格 " + pass + "人"));
        }
        if (die > 0) {
            entries.add(new PieEntry((float) die / total, "挂科 " + die + "人"));
        }
        PieDataSet dataSet = new PieDataSet(entries, null);
        List<Integer> colors = new ArrayList<>();
        TypedArray colorResource = getResources().obtainTypedArray(R.array.colors);
        for (int i = 0; i < colorResource.length(); i++) {
            colors.add(colorResource.getColor(i, 0));
        }
        colorResource.recycle();
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12.f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.8f);
        dataSet.setValueLinePart2Length(0.3f);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        DecimalFormat df = new DecimalFormat("0.00%");
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return df.format(value);
            }
        });
        PieData pieData = new PieData(dataSet);

        PieChart chart = view.findViewById(R.id.pie_chart);
        chart.setData(pieData);
        chart.setDrawEntryLabels(true);
        chart.getDescription().setEnabled(false);
        chart.setHoleRadius(30f);
        chart.setTransparentCircleRadius(40f);
        chart.setExtraOffsets(20, 20, 20, 20);
        chart.animateY(500, Easing.EaseInOutCubic);

        chart.setEntryLabelColor(Color.BLACK);
        chart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        chart.invalidate();
        return view;
    }
}
