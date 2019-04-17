package com.example.fzuscore;

import android.os.Parcel;
import android.os.Parcelable;

public class SubjectForCard implements Parcelable {
    String name;
    int term;
    double excellent;
    double pass;
    double average;

    double highest;
    double lowest;

    public SubjectForCard(String name, int term,double excellent, double pass, double average, double highest, double lowest) {
        this.name = name;
        this.term = term;
        this.excellent = excellent;
        this.pass = pass;
        this.average = average;
        this.highest = highest;
        this.lowest = lowest;
    }

    protected SubjectForCard(Parcel in) {
        name = in.readString();
        term = in.readInt();
        excellent = in.readDouble();
        pass = in.readDouble();
        average = in.readDouble();
        highest = in.readDouble();
        lowest = in.readDouble();
    }

    public static final Creator<SubjectForCard> CREATOR = new Creator<SubjectForCard>() {
        @Override
        public SubjectForCard createFromParcel(Parcel in) {
            return new SubjectForCard(in);
        }

        @Override
        public SubjectForCard[] newArray(int size) {
            return new SubjectForCard[size];
        }
    };

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(term);
        dest.writeDouble(excellent);
        dest.writeDouble(pass);
        dest.writeDouble(average);
        dest.writeDouble(highest);
        dest.writeDouble(lowest);
    }
}
