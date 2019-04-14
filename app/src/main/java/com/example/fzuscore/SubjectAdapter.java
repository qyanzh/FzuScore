package com.example.fzuscore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {

        View subjectView;
        TextView textViewSubject;
        TextView textViewMyScore;
        TextView textViewAvrScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectView = itemView;
            textViewSubject = itemView.findViewById(R.id.subject_name);
            textViewMyScore = itemView.findViewById(R.id.score_mine);
            textViewAvrScore = itemView.findViewById(R.id.score_aver);
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
        final ViewHolder holder = new ViewHolder(view);
        holder.subjectView.setOnClickListener(v->{
            Toast.makeText(mContext, "something", Toast.LENGTH_SHORT).show();
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Subject subject = mSubjectList.get(i);
        viewHolder.textViewSubject.setText(subject.getName());
        viewHolder.textViewMyScore.setText(String.valueOf(subject.getMyScore()));
        viewHolder.textViewAvrScore.setText(String.valueOf(subject.getAvrScore()));
    }

    @Override
    public int getItemCount() {
        return mSubjectList.size();
    }

}
