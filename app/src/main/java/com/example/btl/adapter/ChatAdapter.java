package com.example.btl.adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.RoomActivity;
import com.example.btl.fragment.ChatFragment;
import com.example.btl.model.Chat;
import com.example.btl.utils.ClickItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {
    private List<Chat> chatList = new ArrayList<>();
    private ClickItem clickItem;


    public ChatAdapter(List<Chat> chats) {
        this.chatList = chats;

    }

    public void setOnClickItem(ClickItem onClickItem1) {
        clickItem = onClickItem1;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false));
        } else {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Chat model = chatList.get(position);
        String mess = model.getMessenger();
        String url = model.getUrl();
        String name = model.getName();
        String m = "<font color='yellow'>★</font>";
        if (RoomActivity.UID_KEY.equals(model.getUid())) {
            if (!model.getUid().equals(ChatFragment.UID)) {
                name = m + name;
            }
        }

        String c = "<b> <font color ='#4AAFFF'>" + mess + "</color></b>";
        String n = "<b> <font color ='#4AAFFF'>" + name + "</color></b>";

        String c2 = "<b>" + mess + "</b>";
        String n2 = "<b>" + name + "</b>";

        if (model.getUid().equals(ChatFragment.UID)) {
            setTextView(holder, n, c);
        } else {
            setTextView(holder, n2, c2);
        }
        Glide.with(holder.itemView.getContext())
                .load(url)
                .centerCrop()
                .error(R.mipmap.demo)
                .into(holder.mAvatar);
        holder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItem.click(position);
            }
        });


    }

    private void setTextView(VH vh, String n, String c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            vh.name.setText(Html.fromHtml(n, Html.FROM_HTML_MODE_LEGACY));
            vh.messenger.setText(Html.fromHtml(c, Html.FROM_HTML_MODE_LEGACY));
        } else {
            vh.name.setText(Html.fromHtml(n));
            vh.messenger.setText(Html.fromHtml(c));
        }
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        String s = chatList.get(position).getUid();
        String uid = ChatFragment.UID;
        if (s.equals(uid)) {
            return 0;
        } else {
            return 1;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView messenger;
        private CircleImageView mAvatar;

        public VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            messenger = itemView.findViewById(R.id.messenger);
            mAvatar = itemView.findViewById(R.id.mAvatar);
        }
    }


}
