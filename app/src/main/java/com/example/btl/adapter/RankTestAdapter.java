package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.model.Point;
import com.example.btl.utils.ClickItem;

import java.util.ArrayList;
import java.util.List;

public class RankTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Point> rankList = new ArrayList<>();
    private ClickItem clickItem;
    public RankTestAdapter(List<Point> rankList) {
        this.rankList = rankList;
        notifyDataSetChanged();
    }
    public void setOnClickItem(ClickItem onClickItem1) {
        clickItem = onClickItem1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH2(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VH2 hd = (VH2) holder;
        hd.rank.setText(String.valueOf(position + 1));
        hd.name.setText(rankList.get(position).getName());
        hd.point.setText(rankList.get(position).getScore());
        hd.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItem.click(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return rankList != null ? rankList.size() : 0;
    }


    static class VH2 extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView point;
        private TextView rank;


        public VH2(@NonNull View itemView) {
            super(itemView);
            point = itemView.findViewById(R.id.txtScore);
            name = itemView.findViewById(R.id.txtName);
            rank = itemView.findViewById(R.id.txtRank);

        }
    }

}
