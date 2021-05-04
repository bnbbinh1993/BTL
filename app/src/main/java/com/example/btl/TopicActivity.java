package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.btl.adapter.AdapterTopic;
import com.example.btl.model.Topic;
import com.example.btl.utils.ClickItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopicActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private GoogleSignInAccount acct;
    private RecyclerView recyclerView;
    private AdapterTopic adapterTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        init();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Create room");
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());


        adapterTopic = new AdapterTopic(getDataByFirebase());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapterTopic);
        adapterTopic.ClickListioner(new ClickItem() {
            @Override
            public void click(int position) {
                Toast.makeText(TopicActivity.this, "Oke", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.mRecyclerview);

    }


    private List<Topic> getDataByFirebase() {
        List<Topic> result = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Topic T = snapshot.getValue(Topic.class);
                        result.add(T);
                    }
                } else {
                    Toast.makeText(TopicActivity.this, "Null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add) {
            Toast.makeText(this, "Oke", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}