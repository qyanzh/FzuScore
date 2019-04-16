package com.example.fzuscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    Switch switchDisplayPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences spf =getSharedPreferences("info", MODE_PRIVATE);
        if(spf.getBoolean("logined",false)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        // Set up the login form.
        mAccountNumber = findViewById(R.id.et_account_number);
        mPassword = findViewById(R.id.et_password);
        switchDisplayPassword = findViewById(R.id.switch_displayPassword);
        switchDisplayPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab_sign_in);
        fab.setOnClickListener(v -> sendRequestWithOkHttp());
    }

    void sendRequestWithOkHttp() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                try {
                    LoginAccess loginAccess = new LoginAccess(mAccountNumber.getText().toString(), mPassword.getText().toString());
                    Gson gson = new Gson();
                    String json = gson.toJson(loginAccess);
                    String url = "http://47.112.10.160:3389/api/login";
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    System.out.println("**********");
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void parseJSONWithJSONObject(String jsonData) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    int isSuccess = jsonObject.getInt("is_success");
                    String message = jsonObject.getString("message");
                    Log.d("TAG", "message: " + message);
                    Log.d("TAG", "Success: " + isSuccess);
                    //Looper.prepare();
                    if (isSuccess == 1) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        });
                        JSONObject initData = jsonObject.getJSONObject("data");
                        SharedPreferences.Editor spf =getSharedPreferences("info", MODE_PRIVATE).edit();
                        spf.putBoolean("logined", true);
                        spf.putString("user_account",mAccountNumber.getText().toString());
                        spf.putString("user_name", initData.getString("student_name"));
                        spf.apply();
                        spf.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (isSuccess == 0) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            mPassword.setText("");
                        });
                    }
                    //Looper.loop();
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


