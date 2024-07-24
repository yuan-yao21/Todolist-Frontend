package com.stu.minote;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stu.minote.databinding.ActivityRegisterBinding;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.util.UserUtils;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.title.tvTitle.setText("注册");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString();
                String psd = binding.etPsd.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(psd)) {
                    Toast.makeText(RegisterActivity.this, "请输入账号或密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                register(name, psd);
            }
        });
    }

    private void register(String name, String psd) {
        JsonObject parmas = new JsonObject();
        parmas.addProperty("username", name);
        parmas.addProperty("password", psd);

        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .register(parmas)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("register", response.code() + "");
                        if (response.code() == 409) {
                            Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("register", "onFailure");

                    }
                });
    }
}