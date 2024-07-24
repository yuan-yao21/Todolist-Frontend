package com.stu.minote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stu.minote.databinding.ItemMainTypeBinding;
import com.stu.minote.databinding.ItemTypeBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.MyViewHolder> {
    ItemTypeBinding itemBinding;

    private Context context;
    private List<String> examBeens;

    OnItemClickListener onItemClickListener;

    public TypeListAdapter(Context context, List<String> examBeens, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.examBeens = examBeens;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClicked(int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemBinding = ItemTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);


        MyViewHolder holder = new MyViewHolder(itemBinding);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.itemErrorQuestionBinding.tvName.setText(examBeens.get(position));
        holder.itemErrorQuestionBinding.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClicked(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return examBeens.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemTypeBinding itemErrorQuestionBinding;

        public MyViewHolder(ItemTypeBinding itemErrorQuestionBinding) {
            super(itemErrorQuestionBinding.getRoot());
            this.itemErrorQuestionBinding = itemErrorQuestionBinding;
        }
    }

}
