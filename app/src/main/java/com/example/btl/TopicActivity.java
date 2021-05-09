package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.btl.adapter.AdapterTopic;
import com.example.btl.model.Topic;
import com.example.btl.utils.ClickItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopicActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private GoogleSignInAccount acct;
    private RecyclerView recyclerView;
    private AdapterTopic adapterTopic;
    private List<Topic> topicList = new ArrayList<>();
    private ProgressBar progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        init();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Topic");
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        adapterTopic = new AdapterTopic(topicList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapterTopic);
        getDataByFirebase();
        adapterTopic.setOnClickItem(new ClickItem() {
            @Override
            public void click(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TopicActivity.this);
                View view = LayoutInflater.from(TopicActivity.this).inflate(R.layout.topic_click_dialog, null);
                builder.setView(view);
                MaterialButton btnTest = view.findViewById(R.id.btnTest);
                MaterialButton btnCreate = view.findViewById(R.id.btnCreate);
                AlertDialog dialog = builder.create();
                dialog.show();
                btnTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TopicActivity.this, TestActivity.class);
                        intent.putExtra("_topic_id", topicList.get(position).getId());
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TopicActivity.this, CreateRoomActivity.class);
                        intent.putExtra("_topic_id", topicList.get(position).getId());
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });


            }
        });

    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.mRecyclerview);
        progress_bar = findViewById(R.id.progress_bar);

    }


    private void getDataByFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Topic T = s.getValue(Topic.class);
                    topicList.add(T);
                }

                adapterTopic.notifyDataSetChanged();
                progress_bar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopicActivity.this, "Null", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add) {
            startActivity(new Intent(TopicActivity.this, CreateQuestionActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getDataByFirebase();
    }

}