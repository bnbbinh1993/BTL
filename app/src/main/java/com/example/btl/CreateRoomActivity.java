
 package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateRoomActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText nameRoom;
    private EditText passwordRoom;
    private EditText topicId;
    private EditText timeTest;
    private MaterialButton btnCreate;
    private GoogleSignInAccount account;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        init();
        initAction();
    }

    private void initAction() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Create room");
        Intent intent = getIntent();
        if (intent != null) {
            topicId.setText(intent.getStringExtra("_topic_id"));
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });

    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        nameRoom = findViewById(R.id.nameRoom);
        passwordRoom = findViewById(R.id.passwordRoom);
        topicId = findViewById(R.id.topicId);
        timeTest = findViewById(R.id.timeTest);
        btnCreate = findViewById(R.id.btnCreate);
    }

    private void create() {
        String name = nameRoom.getText().toString().trim();
        String pass = passwordRoom.getText().toString().trim();
        String topic = topicId.getText().toString().trim();
        String time = timeTest.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
            nameRoom.requestFocus();
        } else if (pass.isEmpty()) {
            Toast.makeText(this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
            passwordRoom.requestFocus();
        } else if (time.isEmpty()) {
            Toast.makeText(this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
            timeTest.requestFocus();
        } else if (topic.isEmpty()) {
            Toast.makeText(this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
            topicId.requestFocus();
        } else {
            DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference().child("topic");
            topicRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(topic)) {
                        if (account != null) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("room")) {

                                        reference.child("room").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                            @Override
                                            public void onSuccess(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot != null) {
                                                    String id = "";
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                                    }
                                                    if (user != null) {
                                                        Map<String, String> map = new HashMap<>();
                                                        map.put("uid", user.getUid());
                                                        map.put("name", name);
                                                        map.put("password", pass);
                                                        map.put("topic", topic);
                                                        map.put("time", time);
                                                        map.put("id", String.valueOf(Integer.parseInt(id) + 1));
                                                        map.put("isPlay", "0");
                                                        map.put("isCount", "1");
                                                        map.put("isCheck", "0");
                                                        map.put("isStop", "0");
                                                        map.put("author", account.getDisplayName());
                                                        int key = Integer.parseInt(id) + 1;
                                                        reference.child("room").child(String.valueOf(Integer.parseInt(id) + 1)).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent intent = new Intent(CreateRoomActivity.this, RoomActivity.class);
                                                                intent.putExtra("id_room", String.valueOf(key));
                                                                finish();
                                                                startActivity(intent);
                                                                Toast.makeText(CreateRoomActivity.this, "isSuccessful!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        if (user != null) {
                                            Map<String, String> map = new HashMap<>();
                                            map.put("uid", user.getUid());
                                            map.put("name", name);
                                            map.put("password", pass);
                                            map.put("topic", topic);
                                            map.put("time", time);
                                            map.put("id", "1000");
                                            map.put("isCount", "1");
                                            map.put("isCheck", "0");
                                            map.put("isPlay", "0");
                                            map.put("isStop", "0");
                                            map.put("author", account.getDisplayName());
                                            reference.child("room").child("1000").setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(CreateRoomActivity.this, RoomActivity.class);
                                                    intent.putExtra("id_room", "1000");
                                                    finish();
                                                    startActivity(intent);
                                                    Toast.makeText(CreateRoomActivity.this, "isSuccessful!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}