package com.example.fzuscore;

import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestUtils {
    public interface ResponseListener {
        void onResponseSuccess();

        void onResponseFailed();
    }

    public static final String SERVER = "http://47.112.10.160:3389/api/";
    public static final String SCORE = "score";
    public static final String SUBJECT = "subject";

    public static String getJSON(String api, JSONObject requestJSON, ResponseListener listener) {
        long requestFrom = Calendar.getInstance().getTimeInMillis();
        final StringBuilder responseData = new StringBuilder();
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .build();
                String url = SERVER + api;
                RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), requestJSON.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                responseData.append(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        boolean error = false;
        while ("".contentEquals(responseData)) {
            if (Calendar.getInstance().getTimeInMillis() - requestFrom > 2000) {
                if (listener != null) {
                    listener.onResponseFailed();

                }
                error = true;
                break;
            }
        }
        if (!error) {
            if (listener != null) {
                listener.onResponseSuccess();
            }
        }
        return responseData.toString();
    }
}
