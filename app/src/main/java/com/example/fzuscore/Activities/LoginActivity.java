package com.example.fzuscore.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.fzuscore.R;
import com.example.fzuscore.Utils.Crypt;
import com.example.fzuscore.Utils.RequestUtils;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText mAccountNumber;
    private EditText mPassword;
    Switch switchDisplayPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences spf = getSharedPreferences("info", MODE_PRIVATE);
        if (spf.getBoolean("logined", false)) {
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

        try {
            JSONObject json = new JSONObject();
            Crypt crypt = new Crypt();
            json.put("student_id", mAccountNumber.getText().toString());
            System.out.println("encode:" + new String(crypt.encrypt_string(mPassword.getText().toString())));
            json.put("student_password", new String(crypt.encrypt_string(mPassword.getText().toString())));
            String responseData = RequestUtils.getJSONByPost("login", json, null);
            System.out.println(responseData);
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
            if (isSuccess == 1) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show());
                JSONObject initData = jsonObject.getJSONObject("data");
                SharedPreferences.Editor spf = getSharedPreferences("info", MODE_PRIVATE).edit();
                int isMonitor = initData.getInt("is_monitor");
                spf.putBoolean("logined", true);
                spf.putBoolean("isMonitor", isMonitor == 1);
                spf.putString("user_account", mAccountNumber.getText().toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}