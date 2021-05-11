package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.adapter.AdapterUser;
import com.example.btl.model.Topic;
import com.example.btl.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class TestActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialButton btnNext;
    private MaterialButton btnFinish;
    private Topic model = new Topic();
    private int position = 1;
    private int size = 0;
    private long keyTime = 1000;
    private TextView txtQuestion, txtAnswerA, txtAnswerB, txtAnswerC, txtAnswerD, txtTotal, txtTime;
    private LinearLayout layoutA, layoutB, layoutC, layoutD;
    private CountDownTimer downTimer;
    private CountDownTimer postDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
        initUtils();
        actionClick();
    }

    private void initUtils() {
        Intent intent = getIntent();
        if (intent != null) {
            String id_topic = intent.getStringExtra("_topic_id");
            showPlay(id_topic);

        }
    }


    private void showPlay(String id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic").child(id);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(Topic.class);
                size = (int) dataSnapshot.child("Question").getChildrenCount();
                isPlay();
            }
        });


    }

    private void setPostDelay() {
        lock();
        checkAnwser();
        postDelay = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (position > size) {
                    btnNext.setVisibility(View.GONE);
                    btnFinish.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.GONE);
                }
                if (postDelay != null) {
                    postDelay.cancel();
                    postDelay = null;
                }
            }
        }.start();


    }

    @SuppressLint("SetTextI18n")
    private void isPlay() {
        resetUI();
        updateUI();

    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        txtTotal.setText(position + "/" + size);
        txtQuestion.setText(model.getQuestion().get(position).getTxtQuestion());
        txtAnswerA.setText(model.getQuestion().get(position).getAnswerA());
        txtAnswerB.setText(model.getQuestion().get(position).getAnswerB());
        txtAnswerC.setText(model.getQuestion().get(position).getAnswerC());
        txtAnswerD.setText(model.getQuestion().get(position).getAnswerD());
        keyTime = Long.parseLong(model.getQuestion().get(position).getTime());
        unLock();
        countdown();

    }

    private void countdown() {

        downTimer = new CountDownTimer(keyTime, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {

                if ((int) (millisUntilFinished / 1000 - 1) <= 0) {
                    txtTime.setText("0 sec");

                } else {
                    txtTime.setText((int) (millisUntilFinished / 1000 - 1) + " sec");
                }

            }

            @Override
            public void onFinish() {

                stopCountdown();
                setPostDelay();

            }
        }.start();

    }

    private void stopCountdown() {
        if (downTimer != null) {
            downTimer.cancel();
            downTimer = null;
        }
    }

    private void lock() {
        layoutA.setClickable(false);
        layoutB.setClickable(false);
        layoutC.setClickable(false);
        layoutD.setClickable(false);
    }

    private void unLock() {
        layoutA.setClickable(true);
        layoutB.setClickable(true);
        layoutC.setClickable(true);
        layoutD.setClickable(true);
    }

    private void resetUI() {
        layoutA.setBackgroundResource(R.color.btnA);
        layoutB.setBackgroundResource(R.color.btnB);
        layoutC.setBackgroundResource(R.color.btnC);
        layoutD.setBackgroundResource(R.color.btnD);

        btnNext.setVisibility(View.GONE);
    }

    private void actionClick() {

        layoutA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.btnA);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.test_1);

                stopCountdown();
                setPostDelay();

            }
        });
        layoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.btnB);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.test_1);

                stopCountdown();
                setPostDelay();
            }
        });
        layoutC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.btnC);
                layoutD.setBackgroundResource(R.color.test_1);

                stopCountdown();
                setPostDelay();
            }
        });
        layoutD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.btnD);

                stopCountdown();
                setPostDelay();

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void checkAnwser() {

        switch (model.getQuestion().get(position).getAnswerTrue()) {
            case 1: {
                layoutA.setBackgroundResource(R.color.correct);
                layoutB.setBackgroundResource(R.color.wrong);
                layoutC.setBackgroundResource(R.color.wrong);
                layoutD.setBackgroundResource(R.color.wrong);
                break;
            }
            case 2: {
                layoutA.setBackgroundResource(R.color.wrong);
                layoutB.setBackgroundResource(R.color.correct);
                layoutC.setBackgroundResource(R.color.wrong);
                layoutD.setBackgroundResource(R.color.wrong);
                break;
            }
            case 3: {
                layoutA.setBackgroundResource(R.color.wrong);
                layoutB.setBackgroundResource(R.color.wrong);
                layoutC.setBackgroundResource(R.color.correct);
                layoutD.setBackgroundResource(R.color.wrong);
                break;
            }
            case 4: {
                layoutA.setBackgroundResource(R.color.wrong);
                layoutB.setBackgroundResource(R.color.wrong);
                layoutC.setBackgroundResource(R.color.wrong);
                layoutD.setBackgroundResource(R.color.correct);
                break;
            }
        }

        position++;
    }


    private void init() {

        btnNext = findViewById(R.id.btnNext);
        btnFinish = findViewById(R.id.btnFinish);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtAnswerA = findViewById(R.id.txtAnswerA);
        txtAnswerB = findViewById(R.id.txtAnswerB);
        txtAnswerC = findViewById(R.id.txtAnswerC);
        txtAnswerD = findViewById(R.id.txtAnswerD);
        txtTotal = findViewById(R.id.txtTotal);
        txtTime = findViewById(R.id.txtTime);
        layoutA = findViewById(R.id.layoutA);
        layoutB = findViewById(R.id.layoutB);
        layoutC = findViewById(R.id.layoutC);
        layoutD = findViewById(R.id.layoutD);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountdown();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopCountdown();
    }
}