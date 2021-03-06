package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.btl.adapter.AdapterQuestion;
import com.example.btl.data.DBHelper;
import com.example.btl.data.QsControler;
import com.example.btl.data.QsMd;
import com.example.btl.model.QS;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateQuestionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MaterialButton btnDone;
    private MaterialButton btnNext;
    private MaterialButton btnPush;
    private MaterialButton btnPushData;
    private MaterialCardView cardQuestion;
    private RecyclerView mRecyclerview;
    private AdapterQuestion adapterQuestion;
    private final List<QS> list = new ArrayList<>();
    private RadioButton btnA;
    private RadioButton btnB;
    private RadioButton btnC;
    private RadioButton btnD;
    private EditText question;
    private EditText edtTime;
    private EditText answerA;
    private EditText answerB;
    private EditText answerC;
    private EditText answerD;
    private EditText nameZoom;
    private EditText descriptions;
    private int AnswerTrue = 1;
    private GoogleSignInAccount acct;
    private ProgressDialog progressDialog;
    private List<QsMd> listDT;
    private List<QsMd> md = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        init();
        initAction();

    }

    @SuppressLint("SetTextI18n")
    private void getPersonByGG() {

        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

        }

    }

    private void initAction() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Create Topic");
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
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
                if (list.size() > 0) {
                    final ProgressDialog progressDialog = new ProgressDialog(CreateQuestionActivity.this);
                    progressDialog.setTitle("Uploading...Please wait!");
                    progressDialog.show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic");
                    reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            long id = 1000;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                id = Long.parseLong(Objects.requireNonNull(ds.child("id").getValue()).toString());
                            }
                            id = id + 1;
                            Map<String, String> map = new HashMap<>();
                            String name = nameZoom.getText().toString().trim();
                            String des = descriptions.getText().toString().trim();

                            if (name.isEmpty()) {
                                Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                                nameZoom.requestFocus();
                                progressDialog.dismiss();
                            } else if (des.isEmpty()) {
                                Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                                descriptions.requestFocus();
                                progressDialog.dismiss();
                            } else {
                                map.put("name", name);
                                map.put("descriptions", des);
                                map.put("id", String.valueOf(id));
                                if (acct != null) {
                                    map.put("author", acct.getDisplayName());
                                } else {
                                    map.put("author", "Admin");
                                }

                                String idzoom = String.valueOf(id);
                                DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("topic").child(idzoom);
                                data.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DatabaseReference data1 = FirebaseDatabase.getInstance().getReference().child("topic").child(idzoom).child("Question");
                                        for (int i = 0; i < list.size(); i++) {
                                            QS a = list.get(i);
                                            data1.child(String.valueOf(i + 1)).setValue(a);
                                        }

                                        progressDialog.dismiss();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CreateQuestionActivity.this, "onFailure!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }

                    });
                } else {
                    Toast.makeText(CreateQuestionActivity.this, "No question!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = question.getText().toString().trim();
                String a1 = answerA.getText().toString().trim();
                String a2 = answerB.getText().toString().trim();
                String a3 = answerC.getText().toString().trim();
                String a4 = answerD.getText().toString().trim();
                String t = edtTime.getText().toString().trim();
                if (t.isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    edtTime.requestFocus();
                } else if (q.isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    question.requestFocus();
                } else if (a1.isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    answerA.requestFocus();
                } else if (a2.isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    answerB.requestFocus();
                } else if (a3.isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    answerC.requestFocus();
                } else if (a4.isEmpty()) {
                    Toast.makeText(CreateQuestionActivity.this, "Cannot to blank!", Toast.LENGTH_SHORT).show();
                    answerD.requestFocus();
                } else {

                    int time = (Integer.parseInt(t) + 2) * 1000;

                    list.add(new QS(q, a1, a2, a3, a4, AnswerTrue, String.valueOf(time)));
                    question.setText("");
                    answerA.setText("");
                    answerB.setText("");
                    answerC.setText("");
                    answerD.setText("");
                    edtTime.setText("");
                    question.requestFocus();
                    adapterQuestion.notifyDataSetChanged();
                }


            }
        });


    }

    private void data() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        QsControler model = new QsControler(getApplicationContext());
        listDT = new ArrayList<>();

        try {
            dbHelper.openDataBase();
            dbHelper.createDataBase();
            listDT = model.getTrangBiDBS();
            Log.d("TAG", "initAction: " + listDT.size());
            dbHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        btnPushData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(CreateQuestionActivity.this);
                progressDialog.setTitle("Uploading...Please wait!");
                progressDialog.show();
                for (int i = 1; i < 53; i++) {
                    md.add(listDT.get(i));
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        long id = 1000;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            id = Long.parseLong(Objects.requireNonNull(ds.child("id").getValue()).toString());
                        }
                        id = id + 1;
                        Map<String, String> map = new HashMap<>();
                        String name = "M???ng m??y t??nh";
                        String des = "M???ng m??y t??nh";

                        map.put("name", name);
                        map.put("descriptions", des);
                        map.put("id", String.valueOf(id));
                        if (acct != null) {
                            map.put("author", acct.getDisplayName());
                        } else {
                            map.put("author", "Admin");
                        }

                        String idzoom = String.valueOf(id);
                        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("topic").child(idzoom);
                        data.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DatabaseReference data1 = FirebaseDatabase.getInstance().getReference().child("topic").child(idzoom).child("Question");
                                for (int i = 0; i < md.size(); i++) {
                                    QS a = new QS(md.get(i).getField1(), md.get(i).getField2(), md.get(i).getField3(), md.get(i).getField4(), md.get(i).getField5(), Integer.parseInt(md.get(i).getField6()), "15");
                                    data1.child(String.valueOf(i + 1)).setValue(a);
                                }
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(CreateQuestionActivity.this, "onFailure!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                });
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
                btnD.setChecked(false);
                AnswerTrue = 1;
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(false);
                btnB.setChecked(true);
                btnC.setChecked(false);
                btnC.setChecked(false);
                btnD.setChecked(false);
                AnswerTrue = 2;
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(false);
                btnB.setChecked(false);
                btnC.setChecked(true);
                btnD.setChecked(false);
                AnswerTrue = 3;
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnA.setChecked(false);
                btnB.setChecked(false);
                btnC.setChecked(false);
                btnD.setChecked(true);
                AnswerTrue = 4;
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
        descriptions = findViewById(R.id.descriptions);
        btnD = findViewById(R.id.btnD);
        answerD = findViewById(R.id.edtAnswerD);
        edtTime = findViewById(R.id.edtTime);
        btnPushData = findViewById(R.id.btnPushData);


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