package com.example.fzuscore;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mAccountNumber;
    private EditText mPassword;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mAccountNumber = findViewById(R.id.et_account_number);

        mPassword = findViewById(R.id.et_password);
        FloatingActionButton fab = findViewById(R.id.fab_sign_in);
        fab.setOnClickListener(v -> {
            sendRequestWithOkHttp();
        });
    }
//        fab.setOnClickListener(new View.OnClickListener()) {
//            @Override
//            public void onClick (View view){
//                sendRequestWithOkHttp();
////                OkHttpClient client = new OkHttpClient.Builder()
////                        .connectTimeout(10, TimeUnit.SECONDS)
////                        .writeTimeout(10,TimeUnit.SECONDS)
////                        .readTimeout(20, TimeUnit.SECONDS)
////                        .build();
////                LoginAccess loginAccess = new LoginAccess("00000000", "00000000");
////                Gson gson = new Gson();
////                String json = gson.toJson(loginAccess);
////                String url= "http://47.112.10.160:3389/api/login";
////                RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
////                        , json);
////                Request request = new Request.Builder()
////                        .url(url)
////                        .post(requestBody)
////                        .build();
//            }


    void sendRequestWithOkHttp() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    LoginAccess loginAccess = new LoginAccess("00000000", "00000000");
                    Gson gson = new Gson();
                    String json = gson.toJson(loginAccess);
                    String url = "http://47.112.10.160:3389/api/login";
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                            , json);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    System.out.println(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * Callback received when a permissions request has been completed.
     */


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
}


