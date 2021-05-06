package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private List<Room> roomList = new ArrayList<>();
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

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        getDataByFirebase();


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join();
            }
        });

    }

    private void join() {
        String input = edtJoin.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(JoinActivity.this, "Null", Toast.LENGTH_SHORT).show();
            edtJoin.requestFocus();
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room");
            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(input)) {
                        if (!dataSnapshot.child(input).child("password").getValue().equals("0")) {
                            Toast.makeText(JoinActivity.this, "Có password", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                            builder.setTitle("Input password");
                            View view = LayoutInflater.from(JoinActivity.this).inflate(R.layout.check_password_dialog, null);
                            EditText ip = view.findViewById(R.id.password);
                            builder.setView(view);
                            builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ip.getText().toString().trim().isEmpty()) {
                                        Toast.makeText(JoinActivity.this, "Vui lòng nhập password!", Toast.LENGTH_SHORT).show();
                                        ip.requestFocus();
                                    } else {
                                        if (ip.getText().toString().trim().equals(dataSnapshot.child(input).child("password").getValue())) {
                                            dialog.dismiss();
                                            // đẩy dữ liệu người chơi lên firebase ^^
                                            if (user != null) {
                                                if (account != null) {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(input).child("user").child(account.getId());
                                                    Map<String, String> map = new HashMap<>();
                                                    map.put("uid", user.getUid());
                                                    //nghĩ sau ^^
                                                    //cập nhật hoạt động cửa user
                                                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                startActivity(new Intent(JoinActivity.this, RoomActivity.class));
                                                            } else {
                                                                Toast.makeText(JoinActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

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
                            Dialog dialog = builder.create();
                            dialog.show();


                        } else {
                            Toast.makeText(JoinActivity.this, "Không có password", Toast.LENGTH_SHORT).show();
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
    }

    private void getDataByFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room");
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                roomList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room T = snapshot.getValue(Room.class);
                    roomList.add(T);
                }
                adapterRoom.notifyDataSetChanged();
                progress_bar.setVisibility(View.GONE);
                mRecyclerview.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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
        //reload
    }
}