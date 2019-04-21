package com.example.fzuscore.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.fzuscore.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RadarChartFragment extends Fragment {
    public static RadarChartFragment newInstance(int mode) {
        Bundle args = new Bundle();
        args.putInt("mode", mode);
        RadarChartFragment fragment = new RadarChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    List<String> terms = new ArrayList<>();
    List<List<String>> subjectNamesList = new ArrayList<>();
    List<List<Float>> percentList = new ArrayList<>();
    int currentTermIndex;
    TextView textView;

    int mode;// 0 for personal, 1 for class

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        mode = args.getInt("mode");
        SharedPreferences spf = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        parseJSON(spf.getString("scoreJSON", null));
    }

    private void parseJSON(String scoreJSON) {
        try {
            JSONArray jsonArray = new JSONArray(scoreJSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray subjectsArray = jsonObject.getJSONArray("subjects");
                String term = jsonObject.getString("term");
                terms.add(term);
                List<String> nameList = new ArrayList<>();
                List<Float> percList = new ArrayList<>();
                for (int j = 0; j < subjectsArray.length(); j++) {
                    JSONObject subject = subjectsArray.getJSONObject(j);
                    String name = subject.getString("subject_name");
                    float percentage;
                    if (mode == 0) {
                        float score = subject.getInt("subject_score");
                        int rank = subject.getInt("subject_rank");
                        int amount = subject.getInt("subject_amount");
                        percentage = 100 - (float) rank / amount * 100;
                    } else {
                        percentage = (float) subject.getDouble("subject_perfect") * 100;
                    }
                    nameList.add(name);
                    percList.add(percentage);
                }
                subjectNamesList.add(nameList);
                percentList.add(percList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RadarChart radarChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radar_chart, container, false);
        radarChart = view.findViewById(R.id.personal_analyse_radar);
        textView = view.findViewById(R.id.tv_term_radar);

        setData();

        initChart();
        return view;
    }

    private void initChart() {
        radarChart.animateXY(1400, 1400, Easing.EaseInOutQuad);
        radarChart.getDescription().setEnabled(false);
        radarChart.getDescription().setText("击败同学（%）");
        int pink = getResources().getColor(R.color.colorAccent, null);
        radarChart.getDescription().setTextColor(pink);
        radarChart.getDescription().setTextSize(10f);
        radarChart.setRotationEnabled(false);
        XAxis xAxis = radarChart.getXAxis();
        //xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextSize(10f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int length = subjectNamesList.get(currentTermIndex).size();
                return subjectNamesList.get(currentTermIndex).get((int) value % length);
            }
        });
        xAxis.setTextColor(Color.BLUE);

        YAxis yAxis = radarChart.getYAxis();
//        yAxis.setTypeface(Typeface.DEFAULT);
        //yAxis.setAxisMaximum(100);
        yAxis.setTextSize(9f);
        //yAxis.setInverted(true);
        yAxis.setDrawLabels(false);
        yAxis.setTextColor(Color.BLUE);
        yAxis.setAxisMinimum(-50f);

        Legend l = radarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setEnabled(false);
        l.setTextColor(Color.WHITE);
    }

    private void setData() {
        ArrayList<RadarEntry> entries = new ArrayList<>();
        for (int i = 0; i < percentList.get(currentTermIndex).size(); i++) {
            entries.add(new RadarEntry(percentList.get(currentTermIndex).get(i)));
        }
        RadarDataSet set = new RadarDataSet(entries, "单科排名");
        //     TypedArray colorResource = getResources().obtainTypedArray(R.array.colors);
//        List<Integer> colors = new ArrayList<>();
//        for (int i = 0; i < colorResource.length(); i++) {
//            colors.add(colorResource.getColor(i, 0));
//        }
        int pink = getResources().getColor(R.color.colorAccent, null);
        set.setColor(pink);
        set.setFillColor(pink);
        set.setDrawFilled(true);
        set.setFillAlpha(180);
        set.setLineWidth(2f);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);

//        ArrayList<IRadarDataSet> sets = new ArrayList<>();
//        sets.add(set);

        RadarData data = new RadarData(set);
        // data.setValueTypeface(Typeface.DEFAULT);
        data.setValueTextSize(8f);
        data.setDrawValues(true);

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        textView.setText(terms.get(currentTermIndex));
        radarChart.setData(data);
        radarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                DecimalFormat df = new DecimalFormat("0.00");
                Snackbar.make(getView(), subjectNamesList.get(currentTermIndex).get((int) h.getX()) + " 击败了" + df.format(e.getY()) + "%的同学", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        radarChart.invalidate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_term, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.getItem(1).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_analyse_change_term:
                OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), (options1, option2, options3, v) -> {
                    //返回的分别是三个级别的选中位置
                    currentTermIndex = options1;
                    setData();
                    initChart();
                })
                        .setSubmitText("确定")
                        .setCancelText("取消")
                        .setSelectOptions(currentTermIndex)
                        .build();
                pvOptions.setPicker(terms);
                pvOptions.show();
                break;
        }
        return true;
    }
}
