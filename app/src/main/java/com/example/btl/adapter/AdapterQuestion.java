package com.example.btl.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.model.QS;

import java.util.ArrayList;
import java.util.List;

public class AdapterQuestion extends RecyclerView.Adapter<AdapterQuestion.VH> {
    private List<QS> list = new ArrayList<>();
    private int i = 0;

    public AdapterQuestion(List<QS> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        int j = position + 1;
        holder.txtQuestion.setText("Question " + j + ": " + list.get(position).getTxtQuestion());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        i = position;
        return i;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView txtQuestion;

        public VH(@NonNull View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
        }
    }
}
