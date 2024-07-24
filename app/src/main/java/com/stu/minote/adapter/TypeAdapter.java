package com.stu.minote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stu.minote.databinding.ItemMainTypeBinding;
import com.stu.minote.databinding.ItemNoteBinding;
import com.stu.minote.databinding.ItemTypeBinding;
import com.stu.minote.entity.NoteBeen;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.MyViewHolder> {
    ItemMainTypeBinding itemBinding;

    private int currentPos = 0;
    private Context context;
    private List<String> examBeens;

    OnItemClickListener onItemClickListener;

    public TypeAdapter(Context context, List<String> examBeens, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.examBeens = examBeens;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClicked(int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemBinding = ItemMainTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);


        MyViewHolder holder = new MyViewHolder(itemBinding);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (currentPos == position) {
            holder.itemErrorQuestionBinding.tvName.setTextColor(Color.BLACK);
        }else {
            holder.itemErrorQuestionBinding.tvName.setTextColor(Color.GRAY);
        }

        holder.itemErrorQuestionBinding.tvName.setText(examBeens.get(position));
        holder.itemErrorQuestionBinding.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClicked(position);
                currentPos = position;
                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        return examBeens.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemMainTypeBinding itemErrorQuestionBinding;

        public MyViewHolder(ItemMainTypeBinding itemErrorQuestionBinding) {
            super(itemErrorQuestionBinding.getRoot());
            this.itemErrorQuestionBinding = itemErrorQuestionBinding;
        }
    }

}
