package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public void ClickListioner(ClickItem clickItem) {
        item = clickItem;
    }

    public AdapterTopic(List<Topic> topicList) {
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return topicList != null ? topicList.size() : 0;

    }

    static class VH extends RecyclerView.ViewHolder implements View.OnClickListener {
        public VH(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            item.click(getAdapterPosition());
        }
    }
}
