package com.example.fzuscore;

public class SubjectForCard {
    String name;
    double excellent;
    double pass;
    double average;

    double highest;
    double lowest;

    public SubjectForCard(String name, double excellent, double pass, double average, double highest, double lowest) {
        this.name = name;
        this.excellent = excellent;
        this.pass = pass;
        this.average = average;
        this.highest = highest;
        this.lowest = lowest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getExcellent() {
        return excellent;
    }

    public void setExcellent(double excellent) {
        this.excellent = excellent;
    }

    public double getPass() {
        return pass;
    }

    public void setPass(double pass) {
        this.pass = pass;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getHighest() {
        return highest;
    }

    public void setHighest(double highest) {
        this.highest = highest;
    }

    public double getLowest() {
        return lowest;
    }

    public void setLowest(double lowest) {
        this.lowest = lowest;
    }

}
