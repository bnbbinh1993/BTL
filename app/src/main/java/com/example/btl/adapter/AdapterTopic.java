package com.example.btl.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.model.Topic;
import com.example.btl.utils.ClickItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterTopic extends RecyclerView.Adapter<AdapterTopic.VH> {
    private List<Topic> topicList = new ArrayList<>();
    private static ClickItem item;

    public void setOnClickItem(ClickItem onClickItem1) {
        item = onClickItem1;
    }

    public AdapterTopic(List<Topic> topicList) {
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Topic model = topicList.get(position);

        holder.name.setText("Name: " + model.getName());
        holder.id.setText("ID: " + model.getId());
        holder.author.setText("Master: " + model.getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.click(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return topicList != null ? topicList.size() : 0;

    }

    static class VH extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView id;
        private TextView author;

        public VH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.id);
            author = itemView.findViewById(R.id.author);

        }


    }
}
