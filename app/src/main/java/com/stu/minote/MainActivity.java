package com.stu.minote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.stu.minote.adapter.NoteAdapter;
import com.stu.minote.adapter.TypeAdapter;
import com.stu.minote.databinding.ActivityMainBinding;
import com.stu.minote.entity.NoteBeen;
import com.stu.minote.http.NetService;
import com.stu.minote.http.RetrofitServiceManager;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.http.res.NoteRes;
import com.stu.minote.http.res.TypeRes;
import com.stu.minote.util.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private List<NoteBeen> noteBeens = new ArrayList<>();
    private List<String> types = new ArrayList<>();

    NoteAdapter noteAdapter;
    TypeAdapter typeAdapter;

    private String currentType = "全部";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvNotes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvType.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        noteAdapter = new NoteAdapter(this, noteBeens, new NoteAdapter.OnItemClickListener() {
            @Override
            public void onClicked(int position) {
                Intent intent = new Intent(MainActivity.this,NoteDetailActivity.class);
                intent.putExtra("note",noteBeens.get(position));
                startActivity(intent);
            }
        });
        binding.rvNotes.setAdapter(noteAdapter);

        typeAdapter = new TypeAdapter(this, types, new TypeAdapter.OnItemClickListener() {
            @Override
            public void onClicked(int position) {
                currentType = types.get(position);
                getNoteList(currentType);
            }
        });
        binding.rvType.setAdapter(typeAdapter);

        binding.llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        binding.ivAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTypeActivity.class);
                startActivity(intent);
            }
        });
        binding.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });
        binding.ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getTypeList();
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

                            typeAdapter.notifyDataSetChanged();

                            if (types.size() > 0) {
                                currentType = types.get(0);
                                getNoteList(currentType);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TypeRes> call, Throwable t) {
                        Log.e("getTypes", "onFailure");

                    }
                });
    }

    private void getNoteList(String type) {
        RetrofitServiceManager.getInstance()
                .create(NetService.class)
                .getNotes("Bearer " + UserUtils.getJwt(), type)
                .enqueue(new Callback<NoteRes>() {
                    @Override
                    public void onResponse(Call<NoteRes> call, Response<NoteRes> response) {
                        Log.e("getNotes", response.code() + "");
                        if (response.code() == 200) {
                            noteBeens.clear();
                            noteBeens.addAll(response.body().getNotes());

                            noteAdapter.notifyDataSetChanged();


                        }
                    }

                    @Override
                    public void onFailure(Call<NoteRes> call, Throwable t) {
                        Log.e("getNotes", "onFailure");

                    }
                });
    }
}