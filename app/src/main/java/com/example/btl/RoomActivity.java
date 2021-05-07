package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.btl.adapter.AdapterUser;
import com.example.btl.model.Topic;
import com.example.btl.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoomActivity extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private AdapterUser adapterUser;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        init();
        initUtils();
        getDataByFirebase();


    }

    private void getDataByFirebase() {
        String id_room = getIntent().getStringExtra("id_room");
        Log.d("TAG", "getDataByFirebase: " + id_room);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("user");
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User model = snapshot.getValue(User.class);
                    userList.add(model);
                }
                adapterUser.notifyDataSetChanged();
                Log.d("TAG", "onComplete: " + userList.size());
            }

        });
    }

    private void initUtils() {
        userList = new ArrayList<>();
        adapterUser = new AdapterUser(userList);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerview.setAdapter(adapterUser);
    }

    private void init() {
        mRecyclerview = findViewById(R.id.mRecyclerview);
    }
}