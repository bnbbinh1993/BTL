package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.model.Room;
import com.example.btl.utils.ClickItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterRoom extends RecyclerView.Adapter<AdapterRoom.VH> {
    private List<Room> roomList = new ArrayList<>();
    private static ClickItem clickItem;


    public AdapterRoom(List<Room> roomList) {
        this.roomList = roomList;
    }

    public void setClickItem(ClickItem clickItem) {
        clickItem = clickItem;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        public VH(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            clickItem.click(getAdapterPosition());
        }
    }
}
