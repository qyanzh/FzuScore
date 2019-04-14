package com.example.fzuscore;

public class ScoreRankStudent implements Comparable<ScoreRankStudent>{
    String name;
    double score;
    int number;
    int rank;

    public ScoreRankStudent(String name, double score, int number,int rank) {
        this.name = name;
        this.score = score;
        this.number = number;
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int compareTo(ScoreRankStudent o) {
        return (int)(o.score - this.score);
    }
}
