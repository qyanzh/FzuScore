package com.example.fzuscore;

public class RequestScoreJSON {
    int student_id;
    int term;

    public RequestScoreJSON(int student_id, int term) {
        this.student_id = student_id;
        this.term = term;
    }
}
