package com.example.fzuscore.DataBeans;

import java.util.Comparator;

public class StudentTotalScoreComparator implements Comparator<StudentTotalScore> {
    int currentTermIndex;

    public StudentTotalScoreComparator(int currentTermIndex) {
        this.currentTermIndex = currentTermIndex;
    }

    @Override
    public int compare(StudentTotalScore o1, StudentTotalScore o2) {
        return o2.progressList.get(currentTermIndex) - o1.progressList.get(currentTermIndex);
    }
}
