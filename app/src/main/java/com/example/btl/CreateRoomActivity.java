package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        init();
        initAction();
    }

    private void initAction() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Create room");
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });


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
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic");
            reference.child(topic).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        //create room
                        if (account != null) {
                            Map<String, String> map = new HashMap<>();
                            map.put("name", name);
                            map.put("password", pass);
                            map.put("topic", topic);
                            map.put("time", time);
                            map.put("id", account.getId());
                            map.put("isPlay", "0");
                            map.put("isStop", "0");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Room").child(account.getId());
                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //đẩy vào phòng
                                        Toast.makeText(CreateRoomActivity.this, "isSuccessful!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(CreateRoomActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(CreateRoomActivity.this, "Please double check the topic id!", Toast.LENGTH_SHORT).show();
                        topicId.requestFocus();
                    }
                }
            });
        }
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        nameRoom = findViewById(R.id.nameRoom);
        passwordRoom = findViewById(R.id.passwordRoom);
        topicId = findViewById(R.id.topicId);
        timeTest = findViewById(R.id.timeTest);
        btnCreate = findViewById(R.id.btnCreate);
    }
}