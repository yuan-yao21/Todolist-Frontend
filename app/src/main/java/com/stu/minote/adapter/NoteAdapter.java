package com.stu.minote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.stu.minote.databinding.ItemNoteBinding;
import com.stu.minote.entity.NoteBeen;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {
    ItemNoteBinding itemBinding;

    private Context context;
    private List<NoteBeen> examBeens;

    OnItemClickListener onItemClickListener;
    public NoteAdapter(Context context, List<NoteBeen> examBeens,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.examBeens = examBeens;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClicked(int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemBinding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);


        MyViewHolder holder = new MyViewHolder(itemBinding);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.itemErrorQuestionBinding.tvTitle.setText(examBeens.get(position).getTitle());
        holder.itemErrorQuestionBinding.tvContent.setText(examBeens.get(position).getTextContent());
        holder.itemErrorQuestionBinding.tvTime.setText(examBeens.get(position).getUpdated());

        holder.itemErrorQuestionBinding.llAll.setOnClickListener(new View.OnClickListener() {
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

        private ItemNoteBinding itemErrorQuestionBinding;

        public MyViewHolder(ItemNoteBinding itemErrorQuestionBinding) {
            super(itemErrorQuestionBinding.getRoot());
            this.itemErrorQuestionBinding = itemErrorQuestionBinding;
        }
    }

}
