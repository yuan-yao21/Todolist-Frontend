package com.stu.minote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.stu.minote.adapter.NoteAdapter;
import com.stu.minote.databinding.ActivitySearchBinding;
import com.stu.minote.entity.NoteBeen;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.NoteRes;
import com.stu.minote.util.CommonUtil;
import com.stu.minote.util.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;

    private List<NoteBeen> noteBeens = new ArrayList<>();

    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.rvNotes.setLayoutManager(new GridLayoutManager(this, 2));

        binding.title.tvTitle.setText("搜索");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        noteAdapter = new NoteAdapter(this, noteBeens, new NoteAdapter.OnItemClickListener() {
            @Override
            public void onClicked(int position) {
                Intent intent = new Intent(SearchActivity.this, NoteDetailActivity.class);
                intent.putExtra("note", noteBeens.get(position));
                startActivity(intent);
            }
        });
        binding.rvNotes.setAdapter(noteAdapter);

        binding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = binding.etKey.getText().toString();

                if (TextUtils.isEmpty(key)) {
                    return;
                }

                getNoteList(key);
                CommonUtil.hide(SearchActivity.this);
            }
        });
    }

    private void getNoteList(String key) {
        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .searchNotes("Bearer " + UserUtils.getJwt(), key)
                .enqueue(new Callback<NoteRes>() {
                    @Override
                    public void onResponse(Call<NoteRes> call, Response<NoteRes> response) {
                        Log.e("searchNotes", response.code() + "");
                        if (response.code() == 200) {
                            noteBeens.clear();
                            noteBeens.addAll(response.body().getNotes());

                            noteAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<NoteRes> call, Throwable t) {
                        Log.e("searchNotes", "onFailure");

                    }
                });
    }
}