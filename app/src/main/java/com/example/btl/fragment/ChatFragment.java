package com.example.btl.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.RoomActivity;
import com.example.btl.adapter.ChatAdapter;
import com.example.btl.model.Chat;
import com.example.btl.model.Profile;
import com.example.btl.utils.ClickItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.getSystemService;

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

        chatAdapter.setOnClickItem(new ClickItem() {
            @Override
            public void click(int position) {
                if (!chatList.get(position).getUid().equals(user.getUid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    View view = LayoutInflater.from(Objects.requireNonNull(getContext())).inflate(R.layout.show_user_dialog, null);
                    TextView txtN = view.findViewById(R.id.txtN);
                    TextView txtFB = view.findViewById(R.id.txtFB);
                    TextView txtEmail = view.findViewById(R.id.txtEmail);
                    TextView txtSDT = view.findViewById(R.id.txtSDT);
                    CircleImageView mAvatar = view.findViewById(R.id.mAvatar);
                    builder.setView(view);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(chatList.get(position).getUid());
                    reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Profile md = dataSnapshot.getValue(Profile.class);
                            assert md != null;
                            txtN.setText(md.getPersonName());
                            txtFB.setText(md.getPersonFB());
                            txtEmail.setText(md.getPersonEmail());
                            txtSDT.setText(md.getPersonPhone());
                            Glide.with(getContext())
                                    .load(md.getPersonPhoto())
                                    .centerCrop()
                                    .into(mAvatar);
                            txtFB.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    copy(getContext(), txtFB.getText().toString());
                                }
                            });
                            txtN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    copy(getContext(), txtN.getText().toString());
                                }
                            });
                            txtEmail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    copy(getContext(), txtEmail.getText().toString());
                                }
                            });
                            txtSDT.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    copy(getContext(), txtSDT.getText().toString());
                                }
                            });


                        }
                    });
                }
            }
        });

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
                    Toast.makeText(getContext(), "Max length = 60", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("ObsoleteSdkInt")
    private void copy(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);

        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);

        }
        Toast.makeText(context, "Copy successful!", Toast.LENGTH_SHORT).show();
    }


    private void init(View view) {
        btnSend = view.findViewById(R.id.btnSend);
        edtChat = view.findViewById(R.id.edtChat);
        mRecyclerview = view.findViewById(R.id.mRecyclerview);
    }
}