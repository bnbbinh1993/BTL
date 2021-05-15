package com.example.btl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.btl.adapter.AdapterRoom;
import com.example.btl.adapter.AdapterTopic;
import com.example.btl.model.Room;
import com.example.btl.model.Topic;
import com.example.btl.utils.ClickItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JoinActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edtJoin;
    private MaterialButton btnJoin;
    private RecyclerView mRecyclerview;
    private AdapterRoom adapterRoom;
    private ProgressBar progress_bar;
    private final List<Room> roomList = new ArrayList<>();
    private GoogleSignInAccount account;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        init();
        initAction();

    }

    private void init() {
        mRecyclerview = findViewById(R.id.mRecyclerview);
        toolbar = findViewById(R.id.toolbar);
        edtJoin = findViewById(R.id.edtJoin);
        btnJoin = findViewById(R.id.btnJoin);
        progress_bar = findViewById(R.id.progress_bar);
    }

    private void initAction() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Join room");
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        adapterRoom = new AdapterRoom(roomList);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(this, 1));
        mRecyclerview.setAdapter(adapterRoom);

        adapterRoom.setClickItem(new ClickItem() {
            @Override
            public void click(int position) {
                joinClick(roomList.get(position).getId());
            }
        });
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        getDataByFirebase();

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edtJoin.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    edtJoin.requestFocus();
                } else {
                    joinClick(input);

                }
            }
        });

    }


    private void joinClick(String key) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room");
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(key)) {
                    if (Objects.requireNonNull(dataSnapshot.child(key).child("isStop").getValue()).equals("0")) {
                        if (Objects.equals(dataSnapshot.child(key).child("isPlay").getValue(), "0")) {
                            if (!Objects.requireNonNull(dataSnapshot.child(key).child("password").getValue()).equals("0")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                View view = LayoutInflater.from(JoinActivity.this).inflate(R.layout.check_password_dialog, null);
                                EditText ip = view.findViewById(R.id.password);
                                MaterialButton mConfirm = view.findViewById(R.id.mConfirm);
                                builder.setView(view);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                mConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ip.getText().toString().trim().isEmpty()) {
                                            Toast.makeText(JoinActivity.this, "Vui lòng nhập password!", Toast.LENGTH_SHORT).show();
                                            ip.requestFocus();
                                        } else {
                                            if (ip.getText().toString().trim().equals(dataSnapshot.child(key).child("password").getValue())) {
                                                dialog.dismiss();
                                                if (user != null) {
                                                    if (account != null) {
                                                        if (!user.getUid().equals(Objects.requireNonNull(dataSnapshot.child(key).child("uid").getValue()).toString())) {
                                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(key).child("user").child(user.getUid());
                                                            Map<String, String> map = new HashMap<>();
                                                            map.put("uid", user.getUid());
                                                            map.put("name", account.getDisplayName());
                                                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Intent intent = new Intent(JoinActivity.this, RoomActivity.class);
                                                                        intent.putExtra("id_room", key);
                                                                        intent.putExtra("pass_room", ip.getText().toString());
                                                                        startActivity(intent);
                                                                    } else {
                                                                        Toast.makeText(JoinActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Intent intent = new Intent(JoinActivity.this, RoomActivity.class);
                                                            intent.putExtra("id_room", key);
                                                            intent.putExtra("pass_room", ip.getText().toString());
                                                            startActivity(intent);
                                                        }
                                                    } else {
                                                        Toast.makeText(JoinActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(JoinActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(JoinActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                ip.requestFocus();
                                                dialog.dismiss();
                                            }

                                        }
                                    }
                                });


                            } else {


                                Toast.makeText(JoinActivity.this, "Không có password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(JoinActivity.this, "Phòng đã bắt đầu bạn không thể tham gia!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(JoinActivity.this, "Phòng đã đóng bạn không thể vào!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(JoinActivity.this, "Không có phòng trên", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JoinActivity.this, "Không có phòng trên", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getDataByFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("room").orderByChild("isPrivacy").equalTo("0");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Room T = s.getValue(Room.class);
                    roomList.add(T);
                }
                adapterRoom.notifyDataSetChanged();
                progress_bar.setVisibility(View.GONE);
                mRecyclerview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JoinActivity.this, "Null", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add) {
            startActivity(new Intent(JoinActivity.this, CreateRoomActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}