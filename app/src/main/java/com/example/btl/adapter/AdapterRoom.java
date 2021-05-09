package com.example.btl.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.model.Room;
import com.example.btl.utils.ClickItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterRoom extends RecyclerView.Adapter<AdapterRoom.VH> {
    private List<Room> roomList = new ArrayList<>();
    private static ClickItem item;
    private int i = 0;


    public AdapterRoom(List<Room> roomList) {
        this.roomList = roomList;
    }

    public void setClickItem(ClickItem clickItem) {
        item = clickItem;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Room model = roomList.get(position);

        holder.name.setText("Tên: " + model.getName());
        holder.id.setText("ID: " + model.getId());


        String a = "<p>Trạng thái: <b> <font color ='red'>Kết thúc</color></b></p>";
        String b = "<p>Trạng thái: <b> <font color ='green'>Đang diên ra</color></b></p>";
        String c = "<p>Trạng thái: <b> <font color ='blue'>Đang chờ</color></b></p>";


        if (model.getIsPlay().equals("1")) {
            setTextView(holder, b);
        } else {
            if (model.getIsStop().equals("1")) {
                setTextView(holder, a);
            } else {
                setTextView(holder, c);
            }

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.click(position);
            }
        });

    }

    private void setTextView(VH vh, String txt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            vh.author.setText(Html.fromHtml(txt, Html.FROM_HTML_MODE_LEGACY));
        } else {
            vh.author.setText(Html.fromHtml(txt));
        }
    }

    @Override
    public int getItemViewType(int position) {
        i = position;
        return i;
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
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
