package com.example.btl.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.btl.R;
import com.example.btl.RoomActivity;
import com.example.btl.adapter.ChatAdapter;
import com.example.btl.model.Chat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private ImageButton btnSend;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GoogleSignInAccount account;
    private DatabaseReference chat;
    private EditText edtChat;
    private RecyclerView mRecyclerview;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    public static String UID = "";

    private final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        initUtils();
        getDataFireBase();
    }

    private void getDataFireBase() {
        DatabaseReference messenger = FirebaseDatabase.getInstance().getReference()
                .child("room").child(RoomActivity.id_room).child("chat");
        messenger.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat model = ds.getValue(Chat.class);
                    chatList.add(model);
                }
                chatAdapter.notifyDataSetChanged();
                mRecyclerview.scrollToPosition(chatList.size() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initUtils() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        UID = user.getUid();
        account = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getContext()));

        chat = FirebaseDatabase.getInstance().getReference().child("room").child(RoomActivity.id_room).child("chat");

        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.setAdapter(chatAdapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RoomActivity.isStart) {
                    Chat md = new Chat(account.getDisplayName(), edtChat.getText().toString(), user.getUid(), String.valueOf(user.getPhotoUrl()));
                    chat.push().setValue(md);
                } else {
                    Toast.makeText(getContext(), "So sorry! Started unable to chat!", Toast.LENGTH_SHORT).show();
                }
                edtChat.setText("");

            }
        });
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 60) {
                    Toast.makeText(getContext(), "Max length = 50", Toast.LENGTH_SHORT).show();
                }
                if (s.length() > 0) {
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init(View view) {
        btnSend = view.findViewById(R.id.btnSend);
        edtChat = view.findViewById(R.id.edtChat);
        mRecyclerview = view.findViewById(R.id.mRecyclerview);
    }
}