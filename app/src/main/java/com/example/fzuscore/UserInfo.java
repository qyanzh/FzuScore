package com.example.fzuscore;

public class UserInfo {
    static private int student_id;
    static private String user_name;

    public static void setStudent_id(int student_id) {
        UserInfo.student_id = student_id;
    }

    public static void setUser_name(String user_name) {
        UserInfo.user_name = user_name;
    }

    public static void setInfo(int student_id, String user_name) {
        setStudent_id(student_id);
        setUser_name(user_name);
    }

    public static int getStudent_id() {
        return student_id;
    }

    public static String getUser_name() {
        return user_name;
    }
}
