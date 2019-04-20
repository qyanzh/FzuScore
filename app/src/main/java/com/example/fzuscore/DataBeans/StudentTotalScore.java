package com.example.fzuscore.DataBeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentTotalScore {
    int id;
    String name;

    public StudentTotalScore(int id, String name) {
        this.id = id;
        this.name = name;
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

    List<Double> scoreList = new ArrayList<>();
    List<Integer> rankList = new ArrayList<>();
    List<Integer> progressList = new ArrayList<>();

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
}