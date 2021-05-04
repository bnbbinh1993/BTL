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
import android.widget.EditText;

import com.example.btl.adapter.AdapterTopic;
import com.example.btl.model.Topic;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JoinActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edtJoin;
    private MaterialButton btnJoin;
    private RecyclerView mRecyclerview;
    private AdapterTopic adapterTopic;
    private List<Topic> topicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Create room");


        init();

    }

    private void init() {
        mRecyclerview = findViewById(R.id.mRecyclerview);
        toolbar = findViewById(R.id.toolbar);
        edtJoin = findViewById(R.id.edtJoin);
        btnJoin = findViewById(R.id.btnJoin);
    }

    private void initAction() {

        adapterTopic = new AdapterTopic(topicList);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(this, 1));
        mRecyclerview.setAdapter(adapterTopic);
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