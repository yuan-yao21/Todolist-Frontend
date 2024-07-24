package com.stu.minote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stu.minote.databinding.ActivityLoginBinding;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.util.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString();
                String psd = binding.etPsd.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(psd)) {
                    Toast.makeText(LoginActivity.this, "请输入账号或密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                login(name, psd);

            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent view1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(view1);
            }
        });
    }

    private void login(String name, String psd) {
        JsonObject parmas = new JsonObject();
        parmas.addProperty("username", name);
        parmas.addProperty("password", psd);

        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .login(parmas)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("login", response.code() + "");
                        if (response.code() != 200) {
                            Toast.makeText(LoginActivity.this, "登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            UserUtils.setLogin(true);
                            UserUtils.setJwt(response.body().getJwt());
                            UserUtils.setUserName(name);
                            UserUtils.setPassword(psd);

                            Intent view1 = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(view1);
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("uploadFace", "onFailure");

                    }
                });
    }
}