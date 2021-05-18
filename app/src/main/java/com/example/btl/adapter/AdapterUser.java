package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.model.User;
import com.example.btl.utils.ClickItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.VH> {
    private List<User> userList = new ArrayList<>();
    private ClickItem clickItem;

    public void setOnClickItem(ClickItem onClickItem1) {
        clickItem = onClickItem1;
    }

    public AdapterUser(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.name.setText(userList.get(position).getName());
        String url = userList.get(position).getUrl();
        Glide.with(holder.itemView.getContext())
                .load(url)
                .centerCrop()
                .error(R.mipmap.demo)
                .into(holder.mAvatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItem.click(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView name;
        private CircleImageView mAvatar;

        public VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mAvatar = itemView.findViewById(R.id.mAvatar);
        }
    }
}
