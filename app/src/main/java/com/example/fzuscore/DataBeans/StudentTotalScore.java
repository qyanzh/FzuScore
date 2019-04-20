package com.example.fzuscore.DataBeans;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SmartTable(name = "进步排名")
public class StudentTotalScore implements Comparable<StudentTotalScore> {
    @SmartColumn(id = 1, name = "姓名")
    String name;
    @SmartColumn(id = 2, name = "学号")
    String id_str;
    int id;
    @SmartColumn(id = 3, name = "成绩")
    String currentScore_str;
    @SmartColumn(id = 4, name = "排名")
    int currentRank;
    @SmartColumn(id = 5, name = "上次")
    int currentLastRank;
    @SmartColumn(id = 6, name = "变化")
    int currentProgress;

    DecimalFormat df = new DecimalFormat("0.00");

    public void onTermChanged(int index) {
        currentScore_str = df.format(scoreList.get(index));
        currentRank = rankList.get(index);
        currentLastRank = rankList.get(index + 1);
        currentProgress = progressList.get(index);
    }

    List<Double> scoreList = new ArrayList<>();
    List<Integer> rankList = new ArrayList<>();
    List<Integer> progressList = new ArrayList<>();

    public StudentTotalScore(int id, String name) {
        this.id = id;
        this.name = name;
        this.id_str = "0" + id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<Double> scoreList) {
        this.scoreList = scoreList;
    }

    public List<Integer> getRankList() {
        return rankList;
    }

    public void setRankList(List<Integer> rankList) {
        this.rankList = rankList;
    }

    public List<Integer> getProgressList() {
        return progressList;
    }

    public void setProgressList(List<Integer> progressList) {
        this.progressList = progressList;
    }



    public void calculateProgress() {
        for (int i = 0; i < rankList.size() - 1; i++) {
            progressList.add(rankList.get(i + 1) - rankList.get(i));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentTotalScore that = (StudentTotalScore) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(StudentTotalScore o) {
        return o.currentProgress - this.currentProgress;
    }
}