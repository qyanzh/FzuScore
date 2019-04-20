package com.example.fzuscore.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fzuscore.DataBeans.StudentTotalScore;
import com.example.fzuscore.R;

import java.text.DecimalFormat;
import java.util.List;

public class ProgressRankAdapter extends RecyclerView.Adapter<ProgressRankAdapter.ViewHolder> {

    private List<StudentTotalScore> mStudentList;

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    int currentIndex;

    public ProgressRankAdapter(List<StudentTotalScore> studentList, int currentIndex) {
        mStudentList = studentList;
        this.currentIndex = currentIndex;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudentTotalScore student = mStudentList.get(position);
        System.out.println(student);
        holder.rank.setText((position + 1) + "");
        holder.name.setText(student.getName());
        DecimalFormat df = new DecimalFormat("0.00");
        holder.score.setText(df.format(student.getScoreList().get(currentIndex)));
        df = new DecimalFormat("000000000");
        holder.number.setText(df.format(student.getId()));
        holder.scoreRank.setText(student.getRankList().get(currentIndex) + "");
        holder.lastRank.setText(student.getRankList().get(currentIndex + 1) + "");
        holder.progress.setText(student.getProgressList().get(currentIndex) + "");
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank;
        TextView number;
        TextView name;
        TextView score;
        TextView scoreRank;
        TextView lastRank;
        TextView progress;

        public ViewHolder(View view) {
            super(view);
            rank = view.findViewById(R.id.tv_rank);
            number = view.findViewById(R.id.tv_id);
            name = view.findViewById(R.id.tv_name);
            score = view.findViewById(R.id.tv_score);
            scoreRank = view.findViewById(R.id.tv_score_rank);
            lastRank = view.findViewById(R.id.tv_last_rank);
            progress = view.findViewById(R.id.tv_progress);
        }
    }

}
