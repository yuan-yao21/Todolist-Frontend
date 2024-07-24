package com.stu.minote;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.stu.minote.adapter.TypeListAdapter;
import com.stu.minote.databinding.ActivityAddTypeBinding;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.TypeRes;
import com.stu.minote.util.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class AddTypeActivity extends AppCompatActivity {

    ActivityAddTypeBinding binding;

    private List<String> types = new ArrayList<>();

    private TypeListAdapter typeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        typeListAdapter = new TypeListAdapter(this, types, new TypeListAdapter.OnItemClickListener() {
            @Override
            public void onClicked(int position) {

            }
        });
        binding.rvType.setAdapter(typeListAdapter);

        binding.llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();
            }
        });
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getTypeList();
    }

    private Dialog dialog;
    private EditText etName;

    private void showDialog() {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.DialogTheme);
            // 设置布局
            View view = View.inflate(this, R.layout.dialog_add_type, null);
            dialog.setContentView(view);

            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            etName = dialog.findViewById(R.id.et_name);
            dialog.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = etName.getText().toString();

                    if (TextUtils.isEmpty(name)) {
                        return;
                    }

                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    private void getTypeList() {
        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .getTypes("Bearer " + UserUtils.getJwt())
                .enqueue(new Callback<TypeRes>() {
                    @Override
                    public void onResponse(Call<TypeRes> call, Response<TypeRes> response) {
                        Log.e("getTypes", response.code() + "");
                        if (response.code() == 200) {
                            types.clear();
                            types.addAll(response.body().getCategories());

                            typeListAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFailure(Call<TypeRes> call, Throwable t) {
                        Log.e("getTypes", "onFailure");

                    }
                });
    }
}