package com.stu.minote;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.stu.minote.databinding.ActivityChangePsdBinding;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.util.UserUtils;

public class ChangePsdActivity extends AppCompatActivity {

    ActivityChangePsdBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePsdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.title.tvTitle.setText("修改密码");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String psd = binding.etPsd.getText().toString();
                String psd1 = binding.etPsdConfirm.getText().toString();

                if (TextUtils.isEmpty(psd) || TextUtils.isEmpty(psd1)) {
                    Toast.makeText(ChangePsdActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!psd.equals(psd1)) {
                    Toast.makeText(ChangePsdActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateUser(psd);

            }
        });
    }

    private void updateUser(String psd) {

        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .updatePsd("Bearer " + UserUtils.getJwt(), psd)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("updatePsd", response.code() + "");
                        if (response.code() != 200) {
                            Toast.makeText(ChangePsdActivity.this, "修改失败，请重新修改", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePsdActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            UserUtils.setPassword(psd);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("updatePsd", "onFailure");

                    }
                });
    }
}