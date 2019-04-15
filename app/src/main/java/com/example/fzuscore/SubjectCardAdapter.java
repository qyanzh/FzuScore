package com.example.fzuscore;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SubjectCardAdapter extends RecyclerView.Adapter<SubjectCardAdapter.ViewHolder> {
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
            subjectName = view.findViewById(R.id.card_subject_name);
            subjectExcellent = view.findViewById(R.id.card_subject_excellent);
            subjectPass = view.findViewById(R.id.card_subject_pass);
            subjectAverage = view.findViewById(R.id.card_subject_average);
            subjectHighest = view.findViewById(R.id.card_subject_highest);
            subjectLowest = view.findViewById(R.id.card_subject_lowest);
        }
    }

    public SubjectCardAdapter(List<SubjectForCard> subjectList){
        mSubjectList = subjectList;
    }

    @Override
    public SubjectCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.subject_card,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        SubjectForCard subject = mSubjectList.get(position);
        viewHolder.subjectName.setText(subject.getName());
        viewHolder.subjectExcellent.setText(String.valueOf(subject.getExcellent()));
        viewHolder.subjectPass.setText(String.valueOf(subject.getPass()));
        viewHolder.subjectAverage.setText(String.valueOf(subject.getAverage()));
        viewHolder.subjectHighest.setText(String.valueOf(subject.getHighest()));
        viewHolder.subjectLowest.setText(String.valueOf(subject.getLowest()));
        viewHolder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext,ScoreRankActivity.class);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mSubjectList.size();
    }
}
