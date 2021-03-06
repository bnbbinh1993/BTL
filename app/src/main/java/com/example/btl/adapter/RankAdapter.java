package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.model.Point;
import com.example.btl.model.User;
import com.example.btl.utils.ClickItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Point> rankList = new ArrayList<>();
    private ClickItem clickItem;

    public RankAdapter(List<Point> rankList) {
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
        hd.rank.setText(String.valueOf(position + 4));
        hd.name.setText(rankList.get(position).getName());
        hd.point.setText(rankList.get(position).getScore());

        Glide.with(hd.itemView.getContext())
                .load(rankList.get(position).getUrl())
                .into(hd.mAvatar);

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
        private CircleImageView mAvatar;


        public VH2(@NonNull View itemView) {
            super(itemView);
            point = itemView.findViewById(R.id.txtScore);
            name = itemView.findViewById(R.id.txtName);
            rank = itemView.findViewById(R.id.txtRank);
            mAvatar = itemView.findViewById(R.id.avt);

        }
    }

}
