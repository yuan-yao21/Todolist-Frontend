package com.stu.minote;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stu.minote.databinding.ActivityChangeUserInfoBinding;
import com.stu.minote.entity.UserInfo;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.util.UserUtils;

public class ChangeUserInfoActivity extends AppCompatActivity {

    ActivityChangeUserInfoBinding binding;

    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangeUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (UserInfo) getIntent().getSerializableExtra("user");

        binding.title.tvTitle.setText("修改资料");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.etNickName.setText(user.getNickname());
        binding.etPhone.setText(user.getMobile());
        binding.etSign.setText(user.getBio());

        binding.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickName = binding.etNickName.getText().toString();
                String phone = binding.etPhone.getText().toString();
                String sign = binding.etSign.getText().toString();


                updateUser(nickName, phone, sign);
            }
        });
    }

    private void updateUser(String name, String phone, String sign) {

        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .updateUser("Bearer " + UserUtils.getJwt(),name,phone,sign)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("updateUser", response.code() + "");
                        if (response.code() != 200) {
                            Toast.makeText(ChangeUserInfoActivity.this, "修改失败，请重新修改", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangeUserInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("updateUser", "onFailure");

                    }
                });
    }
}