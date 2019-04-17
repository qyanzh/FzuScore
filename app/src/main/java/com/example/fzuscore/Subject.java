package com.example.fzuscore;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {
    String name;
    double myScore;
    double avrScore;
    int rank;
    int amount;
    double rankPercent;

    protected Subject(Parcel in) {
        name = in.readString();
        myScore = in.readDouble();
        avrScore = in.readDouble();
        rank = in.readInt();
        amount = in.readInt();
        rankPercent = in.readDouble();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(myScore);
        dest.writeDouble(avrScore);
        dest.writeInt(rank);
        dest.writeInt(amount);
        dest.writeDouble(rankPercent);
    }

    public Subject(String name, double myScore, double avrScore,int rank,int amount) {
        this.name = name;
        this.myScore = myScore;
        this.avrScore = avrScore;
        this.rank = rank;
        this.amount = amount;
        this.rankPercent = (double)rank/amount*100;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMyScore() {
        return myScore;
    }

    public void setMyScore(double myScore) {
        this.myScore = myScore;
    }

    public double getAvrScore() {
        return avrScore;
    }

    public void setAvrScore(double avrScore) {
        this.avrScore = avrScore;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getRankPercent() {
        return rankPercent;
    }

    public void setRankPercent(double rankPercent) {
        this.rankPercent = rankPercent;
    }
}
