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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoomActivity extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private AdapterUser adapterUser;
    private List<User> userList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialButton btnCancel, btnStart;
    private String id_room = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        init();
        initUtils();
        getDataByFirebase();
        initPlay();


    }

    private void initPlay() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("isPlay");
                Map<String, String> map = new HashMap<>();
                map.put("isPlay", "1");

                reference.setValue(map);

            }
        });
    }

    private void getDataByFirebase() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User model = s.getValue(User.class);
                    userList.add(model);
                }
                adapterUser.notifyDataSetChanged();
                Log.d("TAG", "onComplete: " + userList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initUtils() {
        id_room = getIntent().getStringExtra("id_room");
        userList = new ArrayList<>();
        adapterUser = new AdapterUser(userList);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(this, 6));
        mRecyclerview.setAdapter(adapterUser);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("uid").getValue().toString().equals(user.getUid())) {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnCancel.setVisibility(View.GONE);
                    btnStart.setVisibility(View.GONE);
                }
            }
        });
    }

    private void init() {
        mRecyclerview = findViewById(R.id.mRecyclerview);
        btnCancel = findViewById(R.id.btnCancel);
        btnStart = findViewById(R.id.btnStart);
    }

    @Override
    public void onBackPressed() {
        removeValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeValue();

    }

    private void removeValue() {
        id_room = getIntent().getStringExtra("id_room");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query aa = ref.child("room").child(id_room).child("user").orderByChild("uid").equalTo(user.getUid());

        aa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot a) {
                for (DataSnapshot appleSnapshot : a.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
                finish();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });


    }
}