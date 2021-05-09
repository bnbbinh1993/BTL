  package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


import java.sql.Time;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class RoomActivity extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private AdapterUser adapterUser;
    private List<User> userList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialButton btnCancel, btnStart, btnNext;
    private String id_room = "";

    private LinearLayout layoutWait;
    private LinearLayout end_game;
    private RelativeLayout layoutPlay;
    private CountDownTimer count;
    private CountDownTimer downTimer;

    private TextView txtCountDownTime;
    private Topic model = new Topic();
    private int position = 1;
    private int answser = 0;
    private int point = 0;
    private long keyTime = 1000;
    private int size = 0;

    private DatabaseReference mPoint;
    private TextView txtQuestion, txtAnswerA, txtAnswerB, txtAnswerC, txtAnswerD, txtTotal, txtTime;
    private LinearLayout layoutA, layoutB, layoutC, layoutD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        init();
        initUtils();
        getDataByFirebase();
        initPlay();
        actionClick();


    }

    private void initPlay() {
        mPoint = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("coin").child(user.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.child("isPlay").getValue()).toString().equals("1")) {
                    if (Objects.requireNonNull(snapshot.child("isStop").getValue()).toString().equals("0")) {
                        count = new CountDownTimer(4000, 1000) {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountDownTime.setText((int) (millisUntilFinished / 1000) + " sec");
                                if ((int) (millisUntilFinished / 1000) == 0) {
                                    txtCountDownTime.setText("Start");
                                }
                            }

                            @Override
                            public void onFinish() {
                                showPlay();
                                count.cancel();
                            }
                        }.start();

                    } else {
                        showStop();
                    }
                } else {
                    //chua chay
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void stopCountdown() {
        if (downTimer != null) {
            downTimer.cancel();
            downTimer = null;
        }
    }


    private void showStop() {
        layoutPlay.setVisibility(View.GONE);
        layoutWait.setVisibility(View.GONE);
        end_game.setVisibility(View.VISIBLE);

    }

    private void showPlay() {
        layoutPlay.setVisibility(View.VISIBLE);
        layoutWait.setVisibility(View.GONE);
        end_game.setVisibility(View.GONE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id_question = Objects.requireNonNull(snapshot.child("topic").getValue()).toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic").child(id_question);
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        model = dataSnapshot.getValue(Topic.class);
                        size = (int) dataSnapshot.child("Question").getChildrenCount();
                        isPlay();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (position > size) {
            mPoint.setValue(point).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    showStop();
                }
            });

            Toast.makeText(this, "End Game!", Toast.LENGTH_SHORT).show();
        } else {
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


    }

    @SuppressLint("SetTextI18n")
    private void isPlay() {
        resetUI();
        updateUI();

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

                if (millisUntilFinished / 1000 == 0) {
                    lock();
                    checkAnwser();
                }
            }

            @Override
            public void onFinish() {
                stopCountdown();
                setPostDelay();

            }
        }.start();

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

    private void setPostDelay() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        position++;
        isPlay();

    }


    private void actionClick() {

        layoutA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.btnA);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.test_1);

                answser = 1;
            }
        });
        layoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.btnB);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.test_1);
                answser = 2;

            }
        });
        layoutC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.btnC);
                layoutD.setBackgroundResource(R.color.test_1);
                answser = 3;

            }
        });
        layoutD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.btnD);
                answser = 4;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void checkAnwser() {

        if (answser == model.getQuestion().get(position).getAnswerTrue()) {
            point += 100;
        }

        int checkout = model.getQuestion().get(position).getAnswerTrue();


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


    }

    private void resetUI() {
        layoutA.setBackgroundResource(R.color.btnA);
        layoutB.setBackgroundResource(R.color.btnB);
        layoutC.setBackgroundResource(R.color.btnC);
        layoutD.setBackgroundResource(R.color.btnD);

        //btnNext.setVisibility(View.GONE);
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
                if (Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString().equals(user.getUid())) {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnCancel.setVisibility(View.GONE);
                    btnStart.setVisibility(View.GONE);
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("isPlay");
                reference.setValue("1");
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void init() {
        mRecyclerview = findViewById(R.id.mRecyclerview);
        btnCancel = findViewById(R.id.btnCancel);
        btnStart = findViewById(R.id.btnStart);
        btnNext = findViewById(R.id.btnNext);
        layoutWait = findViewById(R.id.layoutWait);
        layoutPlay = findViewById(R.id.layoutPlay);
        txtCountDownTime = findViewById(R.id.txtCountDownTime);

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
        end_game = findViewById(R.id.end_game);
    }

    @Override
    public void onBackPressed() {
        stopCountdown();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        stopCountdown();
    }

    private void cancelTimer() {
        if (count != null) {
            count.cancel();
        }


    }

}