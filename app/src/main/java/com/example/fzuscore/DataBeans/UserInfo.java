package com.example.fzuscore.DataBeans;

public class UserInfo {
    static private int student_id;
    static private String user_name;
    static private String student_id_str;
    static private boolean isMonitor;

    public static void setStudent_id(int student_id) {
        UserInfo.student_id = student_id;
    }

    public static void setUser_name(String user_name) {
        UserInfo.user_name = user_name;
    }

    public static String getStudent_id_str() {
        return student_id_str;
    }

    public static void setStudent_id_str(String student_id_str) {
        UserInfo.student_id_str = student_id_str;
    }

    public static void setInfo(String student_id_str, String user_name,boolean isMonitor) {
        setStudent_id_str(student_id_str);
        setUser_name(user_name);
        setStudent_id(Integer.valueOf(student_id_str));
        setIsMonitor(isMonitor);
    }

    public static boolean isIsMonitor() {
        return isMonitor;
    }

    public static void setIsMonitor(boolean isMonitor) {
        UserInfo.isMonitor = isMonitor;
    }

    public static int getStudent_id() {
        return student_id;
    }

    public static String getUser_name() {
        return user_name;
    }
}
