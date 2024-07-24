package com.stu.minote;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.stu.minote.databinding.ActivityAddNoteBinding;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.util.UserUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    ActivityAddNoteBinding binding;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    private String audioPath = "";

    private MediaPlayer mediaPlayer;

    private ActivityResultLauncher<Intent> pickAudioLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mediaPlayer = new MediaPlayer();

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.CAMERA},
                100);

        binding.tvTime.setText(df.format(new Date().getTime()));

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.ivAudioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }

            }
        });
        pickAudioLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedAudioUri = data.getData();
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(selectedAudioUri);
                                File tempFile = File.createTempFile("audio", ".m4a", getCacheDir());
                                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = inputStream.read(buffer)) != -1) {
                                        fos.write(buffer, 0, length);
                                    }
                                }
                                audioPath = tempFile.getAbsolutePath();
                                mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                                mediaPlayer.prepare();
                                binding.ivAudioPlay.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        binding.ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                pickAudioLauncher.launch(intent);
            }
        });
        binding.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog();
            }
        });
        binding.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.etTitle.getText().toString();
                String content = binding.etContent.getText().toString();
                String type = binding.etType.getText().toString();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    Toast.makeText(AddNoteActivity.this, "请输入笔记标题或内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(type)) {
                    type = "全部";
                }

                publish(title, content, type);

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
                    PictureSelector.create(AddNoteActivity.this)
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
                                    Glide.with(AddNoteActivity.this).load(result.get(0).getCompressPath()).into(binding.ivImage);

                                    imagePath = result.get(0).getCompressPath();
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
                    PictureSelector.create(AddNoteActivity.this)
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

                                    Glide.with(AddNoteActivity.this).load(result.get(0).getCompressPath()).into(binding.ivImage);

                                    imagePath = result.get(0).getCompressPath();
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

    private String imagePath = "";
    MultipartBody.Part bodyAudio;
    MultipartBody.Part bodyImage;

    private void publish(String title, String content, String type) {

        RequestBody titleReq = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), content);
        RequestBody typeReq = RequestBody.create(MediaType.parse("text/plain"), type);

        if (!TextUtils.isEmpty(imagePath)) {
            File file = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            bodyImage = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        }

        if (!TextUtils.isEmpty(audioPath)) {
            File fileAudio = new File(audioPath);
            RequestBody requestFileAudio = RequestBody.create(MediaType.parse("multipart/form-data"), fileAudio);
            bodyAudio = MultipartBody.Part.createFormData("audio", fileAudio.getName(), requestFileAudio);

        }

        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .addNote("Bearer " + UserUtils.getJwt(), titleReq, description, typeReq, bodyImage, bodyAudio)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("addNote", response.code() + "");
                        if (response.code() == 201) {
                            Toast.makeText(AddNoteActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddNoteActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("addNote", "onFailure");

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}