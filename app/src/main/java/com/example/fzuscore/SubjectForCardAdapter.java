package com.example.fzuscore;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class SubjectForCardAdapter extends RecyclerView.Adapter<SubjectForCardAdapter.ViewHolder> {
    private Context mContext;
    private List<SubjectForCard> mSubjectList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView subjectName;
        TextView subjectExcellent;
        TextView subjectPass;
        TextView subjectAverage;
        TextView subjectHighest;
        TextView subjectLowest;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            subjectName = view.findViewById(R.id.subject_name);
            subjectExcellent = view.findViewById(R.id.subject_score_mine);
            subjectPass = view.findViewById(R.id.subject_score_aver);
            subjectAverage = view.findViewById(R.id.subject_rank_percent);
            subjectHighest = view.findViewById(R.id.subject_rank);
            subjectLowest = view.findViewById(R.id.card_subject_lowest);
        }
    }

    public SubjectForCardAdapter(List<SubjectForCard> subjectList) {
        mSubjectList = subjectList;
    }

    @Override
    public SubjectForCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.subject_card,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        DecimalFormat df = new DecimalFormat("0.00");
        SubjectForCard subject = mSubjectList.get(position);
        viewHolder.subjectName.setText(subject.getName());
        viewHolder.subjectExcellent.setText(df.format(subject.getExcellent()));
        viewHolder.subjectPass.setText(df.format(subject.getPass()));
        viewHolder.subjectAverage.setText(df.format(subject.getAverage()));
        viewHolder.subjectHighest.setText(df.format(subject.getHighest()));
        viewHolder.subjectLowest.setText(df.format(subject.getLowest()));
        viewHolder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext,ScoreRankActivity.class);
            intent.putExtra("subject_name",subject.getName());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mSubjectList.size();
    }
}
