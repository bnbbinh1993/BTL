package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.model.Point;
import com.example.btl.model.User;

import java.util.ArrayList;
import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Point> rankList = new ArrayList<>();

    public RankAdapter(List<Point> rankList) {
        this.rankList = rankList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_top, parent, false));
        } else {
            return new VH2(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            VH hd = (VH) holder;
            if (position == 0) {
                hd.image.setImageResource(R.drawable.ic_top1);
            } else if (position == 1) {
                hd.image.setImageResource(R.drawable.ic_top2);
            } else if (position == 2) {
                hd.image.setImageResource(R.drawable.ic_top3);
            }

            hd.name.setText(rankList.get(position).getName());
            hd.point.setText(rankList.get(position).getScore());

        } else {
            VH2 hd = (VH2) holder;
            hd.rank.setText(String.valueOf(position+1));
            hd.name.setText(rankList.get(position).getName());
            hd.point.setText(rankList.get(position).getScore());
        }
    }


    @Override
    public int getItemCount() {
        return rankList != null ? rankList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position <= 2) {
            return 1;
        }
        return 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView point;
        private ImageView image;

        public VH(@NonNull View itemView) {
            super(itemView);
            point = itemView.findViewById(R.id.txtScore);
            name = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.image);
        }
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
