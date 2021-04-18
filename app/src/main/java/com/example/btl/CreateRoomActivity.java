package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.btl.adapter.AdapterQuestion;
import com.example.btl.model.QS;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateRoomActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MaterialButton btnDone;
    private RecyclerView mRecyclerview;
    private AdapterQuestion adapterQuestion;
    private List<QS> list = new ArrayList<>();

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

        adapterQuestion = new AdapterQuestion(list);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerview.setAdapter(adapterQuestion);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(new QS("Test"));
                adapterQuestion.notifyDataSetChanged();
            }
        });

    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        btnDone = findViewById(R.id.btnDone);
        mRecyclerview = findViewById(R.id.mRecyclerview);

    }

    private void showDialogCustom() {
        //show ui create question

        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.demo_add, null, false);
        RadioButton btnA = view.findViewById(R.id.btnA);
        RadioButton btnB = view.findViewById(R.id.btnB);
        RadioButton btnC = view.findViewById(R.id.btnC);

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(true);
                btnB.setChecked(false);
                btnC.setChecked(false);
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(false);
                btnB.setChecked(true);
                btnC.setChecked(false);
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(false);
                btnB.setChecked(false);
                btnC.setChecked(true);
            }
        });


        new MaterialAlertDialogBuilder(CreateRoomActivity.this)
                .setTitle("Create question")
                .setMessage("message")
                .setView(view)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add) {
            showDialogCustom();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}