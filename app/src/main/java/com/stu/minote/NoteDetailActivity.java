package com.stu.minote;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.FormBody;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.stu.minote.databinding.ActivityNoteDetailBinding;
import com.stu.minote.entity.NoteBeen;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.http.res.TypeRes;
import com.stu.minote.util.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NoteDetailActivity extends AppCompatActivity {
    ActivityNoteDetailBinding binding;

    private NoteBeen noteBeen;

    private String imagePath = "";

    private String audioPath = "";

    private MediaPlayer mediaPlayer;
    LinearLayout audio;
    private ActivityResultLauncher<Intent> pickAudioLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mediaPlayer = new MediaPlayer();

        noteBeen = (NoteBeen) getIntent().getSerializableExtra("note");

        binding.etTitle.setText(noteBeen.getTitle());
        binding.etContent.setText(noteBeen.getTextContent());

        binding.tvTime.setText(noteBeen.getUpdated());

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        binding.ivAudioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
            }
        });
        binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog alertDialog = new AlertDialog.Builder(NoteDetailActivity.this).
                        setTitle("提醒").
                        setMessage("删除后不可恢复，是否删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                deleteNote();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).
                        create();
                alertDialog.show();
            }
        });
        binding.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = binding.etTitle.getText().toString();
                String content = binding.etContent.getText().toString();
                String type = binding.etType.getText().toString();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteDetailActivity.this, "请输入笔记标题或内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(type)) {
                    Toast.makeText(NoteDetailActivity.this, "请输入分类", Toast.LENGTH_SHORT).show();
                    return;
                }

                update(title, content, type);
            }
        });
        binding.ivLlm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userInput = binding.etContent.getText().toString();
                postRequest(userInput);
            }
        });
        binding.ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog();
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

                                // 重置并释放MediaPlayer
                                if (mediaPlayer.isPlaying() || mediaPlayer.isLooping()) {
                                    mediaPlayer.stop();
                                }
                                mediaPlayer.reset();
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

        getDetail();
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
                    PictureSelector.create(NoteDetailActivity.this)
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
                                    Glide.with(NoteDetailActivity.this).load(result.get(0).getCompressPath()).into(binding.ivImage);

                                    imagePath = result.get(0).getCompressPath();
                                    binding.ivImage.setVisibility(View.VISIBLE);
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
                    PictureSelector.create(NoteDetailActivity.this)
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

                                    Glide.with(NoteDetailActivity.this).load(result.get(0).getCompressPath()).into(binding.ivImage);

                                    imagePath = result.get(0).getCompressPath();
                                    binding.ivImage.setVisibility(View.VISIBLE);
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
    private void postRequest(String userInput) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://116.198.232.203:8000/api/v1/llm/summarynote";

        RequestBody formBody = new FormBody.Builder()
                .add("request_text", userInput)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + UserUtils.getJwt())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> showDialog("请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> showDialog("请求错误: " + response.code()));
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    if (jsonObject.getString("status").equals("success")) {
                        String summary = jsonObject.getString("response");
                        runOnUiThread(() -> showDialog(summary));
                    } else {
                        String error = jsonObject.getString("message");
                        runOnUiThread(() -> showDialog("服务器错误: " + error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    MultipartBody.Part bodyAudio;
    MultipartBody.Part bodyImage;

    private void update(String title, String content, String type) {

        RequestBody titleReq = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), content);
        RequestBody typeReq = RequestBody.create(MediaType.parse("text/plain"), type);
        RequestBody noteIdReq = RequestBody.create(MediaType.parse("text/plain"), noteBeen.getId());

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
                .updateNote("Bearer " + UserUtils.getJwt(), noteIdReq, titleReq, description, typeReq, bodyImage, bodyAudio)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("updateNote", response.code() + "");
                        if (response.code() == 200) {
                            Toast.makeText(NoteDetailActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NoteDetailActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("updateNote", "onFailure");

                    }
                });
    }

    private void getDetail() {
        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .getNoteDetail("Bearer " + UserUtils.getJwt(), noteBeen.getId())
                .enqueue(new Callback<NoteBeen>() {
                    @Override
                    public void onResponse(Call<NoteBeen> call, Response<NoteBeen> response) {
                        Log.e("getNoteDetail", response.code() + "");
                        if (response.code() == 200) {

                            binding.etType.setText(response.body().getCategory());
                            if (!TextUtils.isEmpty(response.body().getPicture())) {
                                Glide.with(NoteDetailActivity.this).load("http://116.198.232.203:8000/media/" + response.body().getPicture()).into(binding.ivImage);
                                binding.ivImage.setVisibility(View.VISIBLE);


                            }
                            if (!TextUtils.isEmpty(response.body().getAudio())) {
                                binding.ivAudioPlay.setVisibility(View.VISIBLE);
                                try {
                                    mediaPlayer.setDataSource("http://116.198.232.203:8000/media/" + response.body().getAudio());
                                    mediaPlayer.prepare();

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<NoteBeen> call, Throwable t) {
                        Log.e("getNoteDetail", "onFailure");

                    }
                });
    }

    private void deleteNote() {

        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .deleteNote("Bearer " + UserUtils.getJwt(), noteBeen.getId())
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        Log.e("login", response.code() + "");
                        if (response.code() != 200) {
                            Toast.makeText(NoteDetailActivity.this, "删除失败，请重新删除", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NoteDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        Log.e("uploadFace", "onFailure");

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