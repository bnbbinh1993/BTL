package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.btl.adapter.AdapterQuestion;
import com.example.btl.model.QS;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateRoomActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MaterialButton btnDone;
    private MaterialButton btnNext;
    private MaterialButton btnPush;
    private MaterialCardView cardQuestion;
    private RecyclerView mRecyclerview;
    private AdapterQuestion adapterQuestion;
    private List<QS> list = new ArrayList<>();
    private RadioButton btnA;
    private RadioButton btnB;
    private RadioButton btnC;
    private EditText question;
    private EditText answerA;
    private EditText answerB;
    private EditText answerC;
    private EditText nameZoom;
    private EditText passwordZoom;
    private int AnswerTrue = 1;


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

        radiobuttonClick();
        btnA.setChecked(true);
        adapterQuestion = new AdapterQuestion(list);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerview.setAdapter(adapterQuestion);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardQuestion.setVisibility(View.GONE);
                btnPush.setVisibility(View.VISIBLE);

            }
        });
        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(CreateRoomActivity.this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Zoom");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        long id = 0;
                        for (DataSnapshot a : dataSnapshot.getChildren()) {
                            id = a.getChildrenCount();
                        }
                        Map<String, String> map = new HashMap<>();

                        String name = nameZoom.getText().toString().trim();
                        String password = passwordZoom.getText().toString().trim();

                        // check sau

                        map.put("name", name);
                        map.put("password", password);
                        String idzoom = String.valueOf(id + 1);
                        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Zoom").child(idzoom);
                        data.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DatabaseReference data1 = FirebaseDatabase.getInstance().getReference().child("Zoom").child(idzoom).child("Question");
                                for (int i = 0; i < list.size(); i++) {
                                    QS a = list.get(i);
                                    data1.child(String.valueOf(i + 1)).setValue(a);
                                }

                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateRoomActivity.this, "onFailure!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateRoomActivity.this, "onFailure!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });


            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = question.getText().toString().trim();
                String a1 = answerA.getText().toString().trim();
                String a2 = answerB.getText().toString().trim();
                String a3 = answerC.getText().toString().trim();
                if (q.isEmpty()) {
                    Toast.makeText(CreateRoomActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    question.requestFocus();
                } else if (a1.isEmpty()) {
                    Toast.makeText(CreateRoomActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    answerA.requestFocus();
                } else if (a2.isEmpty()) {
                    Toast.makeText(CreateRoomActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    answerB.requestFocus();
                } else if (a3.isEmpty()) {
                    Toast.makeText(CreateRoomActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    answerC.requestFocus();
                } else {
                    list.add(new QS(q, a1, a2, a3, AnswerTrue));
                    adapterQuestion.notifyDataSetChanged();
                }


            }
        });

    }

    private void radiobuttonClick() {
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(true);
                btnB.setChecked(false);
                btnC.setChecked(false);
                AnswerTrue = 1;
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(false);
                btnB.setChecked(true);
                btnC.setChecked(false);
                AnswerTrue = 2;
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(false);
                btnB.setChecked(false);
                btnC.setChecked(true);
                AnswerTrue = 3;
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        btnDone = findViewById(R.id.btnDone);
        btnNext = findViewById(R.id.btnNext);
        btnPush = findViewById(R.id.btnPush);
        mRecyclerview = findViewById(R.id.mRecyclerview);
        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        question = findViewById(R.id.edtQuestion);
        answerA = findViewById(R.id.edtAnswerA);
        answerB = findViewById(R.id.edtAnswerB);
        answerC = findViewById(R.id.edtAnswerC);
        cardQuestion = findViewById(R.id.cardQuestion);
        nameZoom = findViewById(R.id.nameZoom);
        passwordZoom = findViewById(R.id.passwordZoom);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add) {
            cardQuestion.setVisibility(View.VISIBLE);
            btnPush.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}