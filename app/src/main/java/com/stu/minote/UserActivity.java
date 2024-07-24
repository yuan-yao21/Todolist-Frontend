package com.stu.minote;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.stu.minote.databinding.ActivityUserBinding;
import com.stu.minote.entity.UserInfo;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.http.res.TypeRes;
import com.stu.minote.util.UserUtils;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

public class UserActivity extends AppCompatActivity {
    ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA},
                100);

        binding.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog();
            }
        });

        binding.tvName.setText(UserUtils.getUserName());
        binding.title.tvTitle.setText("个人中心");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo == null) {
                    Toast.makeText(UserActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(UserActivity.this, ChangeUserInfoActivity.class);
                intent.putExtra("user", userInfo);
                startActivity(intent);
            }
        });
        binding.tvPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserActivity.this, ChangePsdActivity.class);
                startActivity(intent);
            }
        });
        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    private Uri getImageCropUri() {
        File file = new File(getExternalFilesDir(null), "/head/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        return Uri.fromFile(file);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getInfo();
    }


    private UserInfo userInfo;

    private void getInfo() {
        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .getUserInfo("Bearer " + UserUtils.getJwt())
                .enqueue(new Callback<UserInfo>() {
                    @Override
                    public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                        Log.e("getUserInfo", response.code() + "");
                        if (response.code() == 200) {
                            userInfo = response.body();

                            if (!TextUtils.isEmpty(response.body().getNickname())) {
                                binding.tvName.setText(response.body().getNickname());
                                Glide.with(UserActivity.this).load(RetrofitServiceManager.IMAGE_HEAD+userInfo.getHead_image()).into(binding.ivHead);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfo> call, Throwable t) {
                        Log.e("getUserInfo", "onFailure");

                    }
                });
    }


    private Dialog dialog;
    private void showPhotoDialog() {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.DialogTheme);
            // 设置布局
            View view = View.inflate(this, R.layout.popup_take_photo, null);
            dialog.setContentView(view);

            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.findViewById(R.id.tv_avatar_photograph).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PictureSelector.create(UserActivity.this)
                            .openCamera(SelectMimeType.ofImage())
                            .setCompressEngine(new CompressFileEngine() {
                                @Override
                                public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
                                    Luban.with(context).load(source).ignoreBy(100)
                                            .setCompressListener(new OnNewCompressListener() {
                                                @Override
                                                public void onStart() {

                                                }

                                                @Override
                                                public void onSuccess(String source, File compressFile) {
                                                    if (call != null) {
                                                        call.onCallback(source, compressFile.getAbsolutePath());
                                                    }
                                                }

                                                @Override
                                                public void onError(String source, Throwable e) {
                                                    if (call != null) {
                                                        call.onCallback(source, null);
                                                    }
                                                }
                                            }).launch();
                                }
                            })
                            .forResult(new OnResultCallbackListener<LocalMedia>() {
                                @Override
                                public void onResult(ArrayList<LocalMedia> result) {
                                    Glide.with(UserActivity.this).load(result.get(0).getCompressPath()).into(binding.ivHead);

                                    updateHead(result.get(0).getCompressPath());
                                }

                                @Override
                                public void onCancel() {

                                }
                            });

                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_avatar_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //从相册中选取
                    PictureSelector.create(UserActivity.this)
                            .openSystemGallery(SelectMimeType.ofImage())
                            .setCompressEngine(new CompressFileEngine() {
                                @Override
                                public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
                                    Luban.with(context).load(source).ignoreBy(100)
                                            .setCompressListener(new OnNewCompressListener() {
                                                @Override
                                                public void onStart() {

                                                }

                                                @Override
                                                public void onSuccess(String source, File compressFile) {
                                                    if (call != null) {
                                                        call.onCallback(source, compressFile.getAbsolutePath());
                                                    }
                                                }

                                                @Override
                                                public void onError(String source, Throwable e) {
                                                    if (call != null) {
                                                        call.onCallback(source, null);
                                                    }
                                                }
                                            }).launch();
                                }
                            })
                            .forSystemResult(new OnResultCallbackListener<LocalMedia>() {
                                @Override
                                public void onResult(ArrayList<LocalMedia> result) {

                                    Glide.with(UserActivity.this).load(result.get(0).getCompressPath()).into(binding.ivHead);

                                    updateHead(result.get(0).getCompressPath());
                                }

                                @Override
                                public void onCancel() {

                                }
                            });

                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_avatar_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    private void updateHead(String path){
        File file = new File(path);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("head_image", file.getName(), requestFile);

        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .updateUserHead("Bearer " + UserUtils.getJwt(),body)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("updateUserHead", response.code() + "");
                        if (response.code() == 200) {
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("updateUserHead", "onFailure");

                    }
                });
    }
}