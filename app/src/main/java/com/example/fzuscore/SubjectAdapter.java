package com.example.fzuscore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewSubject;
        TextView textViewScore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.subject_name);
            textViewScore = itemView.findViewById(R.id.score_mine);
        }
    }

    private List<Subject> mSubjectList;
    private Context mContext;
    SubjectAdapter(List<Subject> subjectList) {mSubjectList = subjectList;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.subject_item, viewGroup, false);
        //TODO:finish it.
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
