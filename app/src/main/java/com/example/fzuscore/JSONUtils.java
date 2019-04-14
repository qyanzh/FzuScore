package com.example.fzuscore;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONUtils {
    public static int[] getIntArrayFromJSONArray(JSONArray termJSONArray) throws JSONException {
        int[] termList = new int[termJSONArray.length()];
        for (int i = 0; i < termJSONArray.length();i++) {
            termList[i] = termJSONArray.getInt(i);
        }
        return termList;
    }
}
