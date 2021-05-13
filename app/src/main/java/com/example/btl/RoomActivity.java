package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.btl.adapter.AdapterUser;
import com.example.btl.model.Point;
import com.example.btl.model.Topic;
import com.example.btl.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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


import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import java.util.Objects;


public class RoomActivity extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private AdapterUser adapterUser;
    private List<User> userList;
    private List<Point> scoreList;
    private FirebaseUser user;
    private LinearLayout end_game;
    private LinearLayout layoutRank;
    private RelativeLayout layoutPlay;
    private RelativeLayout layoutWait;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private CountDownTimer count;
    private CountDownTimer downTimer;
    private CountDownTimer postDelay;
    private CountDownTimer ct;
    private TextView txtRoomName;
    private TextView txtCountNumber;
    private TextView txtNameTop1;
    private TextView txtNameTop2;
    private TextView txtNameTop3;
    private TextView txtScore1;
    private TextView txtScore2;
    private TextView txtScore3;
    private TextView txtPoint;
    private TextView txtRank;
    private ImageButton btnMenu;
    private ImageButton btnCancel;
    private MaterialButton btnNext;
    private MaterialButton benFinish_end;
    private MaterialButton btnOpen;
    private MaterialButton btnPlay;
    private MaterialButton btnSharePin;
    private Topic model = new Topic();
    private final Point pt = new Point();
    private int position = 1;
    private int answer = 0;
    private int point = 0;
    private long keyTime = 1000;
    private int size = 0;
    private boolean isFinish = false;
    private boolean isKey = false;
    private boolean isRestart = false;
    private boolean isStart = false;
    private String id_room = "";
    private GoogleSignInAccount account;
    private ProgressBar progress_bar;
    private String pass_room = "";

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
        isClick();
        isStop();
        isCheckPlayer();
        clear();


    }


    private void isRank() {
        layoutRank.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.INVISIBLE);
        scoreList = new ArrayList<>();
        DatabaseReference rank = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("coin");
        rank.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scoreList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Point md = ds.getValue(Point.class);
                    scoreList.add(md);
                }
                Collections.sort(scoreList);


                if (scoreList.size() > 0) {
                    rl1.setVisibility(View.VISIBLE);
                    rl2.setVisibility(View.GONE);
                    rl3.setVisibility(View.GONE);
                    txtNameTop1.setText(scoreList.get(0).getName());
                    txtScore1.setText(scoreList.get(0).getScore());

                }
                if (scoreList.size() > 1) {
                    rl1.setVisibility(View.VISIBLE);
                    rl2.setVisibility(View.VISIBLE);
                    rl3.setVisibility(View.GONE);
                    txtNameTop2.setText(scoreList.get(1).getName());
                    txtScore2.setText(scoreList.get(1).getScore());

                }
                if (scoreList.size() > 2) {
                    rl1.setVisibility(View.VISIBLE);
                    rl2.setVisibility(View.VISIBLE);
                    rl3.setVisibility(View.VISIBLE);
                    txtNameTop3.setText(scoreList.get(2).getName());
                    txtScore3.setText(scoreList.get(2).getScore());
                }

                for (int i = 0; i < scoreList.size(); i++) {
                    if (user.getUid().equals(scoreList.get(i).getUid())) {
                        txtRank.setText("Ranking: " + (i + 1));
                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isCheckPlayer() {
        DatabaseReference isStop = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        isStop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("coin").getChildrenCount() == (snapshot.child("user").getChildrenCount() + 1)) {
                    isStop.child("isStop").setValue("1");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isStop() {
        DatabaseReference isStop = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        isStop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.equals(snapshot.child("isStop").getValue(), "1")) {
                    isRank();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initPlay() {

        mPoint = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("coin").child(user.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFinish) {
                    if (Objects.requireNonNull(snapshot.child("isPlay").getValue()).toString().equals("1")) {
                        if (Objects.requireNonNull(snapshot.child("isStop").getValue()).toString().equals("0")) {
                            isStart = true;
                            count = new CountDownTimer(4000, 1000) {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    txtRoomName.setText((int) (millisUntilFinished / 1000) + " sec");
                                    if ((int) (millisUntilFinished / 1000) == 0) {
                                        txtRoomName.setText("Start");
                                    }
                                }

                                @Override
                                public void onFinish() {

                                    showPlay();
                                    if (count != null) {
                                        count.cancel();
                                        count = null;
                                    }


                                }
                            }.start();

                        } else {
                            showStop();
                        }
                    } else if (Objects.requireNonNull(snapshot.child("isStop").getValue()).toString().equals("0")) {
                        layoutWait.setVisibility(View.VISIBLE);
                        end_game.setVisibility(View.GONE);
                        layoutPlay.setVisibility(View.GONE);
                    }

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

        stopCountdown();
        cancelTimer();

    }

    private void showPlay() {
        layoutPlay.setVisibility(View.VISIBLE);
        layoutWait.setVisibility(View.GONE);
        end_game.setVisibility(View.GONE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String id_question = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("topic").getValue()).toString();
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
        });


    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (position > size) {
            showStop();
            if (!isKey) {
                DatabaseReference isCheck = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("isCheck");
                isCheck.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        int p = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString());
                        p++;
                        isCheck.setValue(String.valueOf(p));
                    }
                });
            }

            txtPoint.setText("Total score: " + point);

            pt.setName(account.getDisplayName());
            pt.setUid(user.getUid());
            pt.setScore(String.valueOf(point));
            mPoint.setValue(pt);
            isFinish = true;

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
        isRestart = false;
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
        lock();
        checkAnwser();
        postDelay = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (postDelay != null) {
                    postDelay.cancel();
                    postDelay = null;
                }

                position++;
                isPlay();
            }
        }.start();


    }


    private void isClick() {

        layoutA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.btnA);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.test_1);
                answer = 1;
            }
        });
        layoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.btnB);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.test_1);
                answer = 2;

            }
        });
        layoutC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.btnC);
                layoutD.setBackgroundResource(R.color.test_1);
                answer = 3;
            }
        });
        layoutD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutA.setBackgroundResource(R.color.test_1);
                layoutB.setBackgroundResource(R.color.test_1);
                layoutC.setBackgroundResource(R.color.test_1);
                layoutD.setBackgroundResource(R.color.btnD);
                answer = 4;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void checkAnwser() {
        lock();
        if (answer == model.getQuestion().get(position).getAnswerTrue()) {
            point += 100;
        }

        answer = 0;

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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User model = s.getValue(User.class);
                    userList.add(model);
                }
                adapterUser.notifyDataSetChanged();
                txtCountNumber.setText(String.valueOf(userList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void initUtils() {

        layoutWait.setVisibility(View.VISIBLE);

        id_room = getIntent().getStringExtra("id_room");
        pass_room = getIntent().getStringExtra("pass_room");

        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        userList = new ArrayList<>();
        adapterUser = new AdapterUser(userList);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new GridLayoutManager(this, 6));
        mRecyclerview.setAdapter(adapterUser);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("room").child(id_room);
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString().equals(user.getUid())) {
                    isKey = true;
                    btnPlay.setVisibility(View.VISIBLE);
                    btnMenu.setVisibility(View.VISIBLE);
                    btnOpen.setVisibility(View.VISIBLE);
                } else {
                    isKey = false;
                    btnPlay.setVisibility(View.GONE);
                    btnMenu.setVisibility(View.GONE);
                    btnOpen.setVisibility(View.GONE);
                }
                txtRoomName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setVisibility(View.GONE);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("user");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DatabaseReference cnt = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("isCount");
                        cnt.setValue(snapshot.getChildrenCount());

                        if (isKey) {
                            if (!isFinish) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("isPlay");
                                reference.setValue("1");
                                isStart = true;
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        btnSharePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                View view = LayoutInflater.from(RoomActivity.this).inflate(R.layout.pin_view, null);
                TextView txtPin = view.findViewById(R.id.txtPin);
                TextView txtPass = view.findViewById(R.id.txtPass);
                txtPin.setText("ID: " + id_room);
                txtPass.setText("Pass: " + pass_room);
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        benFinish_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeValue();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeValue();
            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dt = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("isRestart");
                dt.setValue("1");

            }
        });


    }

    private void clear() {


        DatabaseReference cl = FirebaseDatabase.getInstance().getReference().child("room").child(id_room).child("isRestart");
        cl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.equals(snapshot.getValue(), "1")) {
                    if (!isRestart) {
                        isRestart = true;
                        AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                        View view = LayoutInflater.from(RoomActivity.this).inflate(R.layout.remake_diaglog, null);
                        TextView txtTm = view.findViewById(R.id.txtTimeRestart);
                        builder.setView(view);
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();

                        ct = new CountDownTimer(11000, 1000) {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtTm.setText(millisUntilFinished / 1000 + "s");
                            }

                            @Override
                            public void onFinish() {
                                dialog.dismiss();
                                removeValue();
                                if (ct != null) {
                                    ct.cancel();
                                    ct = null;
                                }
                            }
                        }.start();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init() {
        mRecyclerview = findViewById(R.id.mRecyclerview);
        btnNext = findViewById(R.id.btnNext);
        layoutPlay = findViewById(R.id.layoutPlay);
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
        txtRoomName = findViewById(R.id.txtRoomName);
        layoutWait = findViewById(R.id.layoutWait);
        btnPlay = findViewById(R.id.btnPlay);
        btnMenu = findViewById(R.id.btnMenu);
        btnCancel = findViewById(R.id.btnCancel);
        txtCountNumber = findViewById(R.id.txtCountNumber);
        btnSharePin = findViewById(R.id.btnSharePin);
        txtNameTop1 = findViewById(R.id.txtNameTop1);
        txtNameTop2 = findViewById(R.id.txtNameTop2);
        txtNameTop3 = findViewById(R.id.txtNameTop3);
        txtScore1 = findViewById(R.id.txtScore1);
        txtScore2 = findViewById(R.id.txtScore2);
        txtScore3 = findViewById(R.id.txtScore3);
        txtPoint = findViewById(R.id.txtPoint);
        txtRank = findViewById(R.id.txtRank);
        layoutRank = findViewById(R.id.layoutRank);
        benFinish_end = findViewById(R.id.btnFinish_end);
        btnOpen = findViewById(R.id.btnOpen);
        progress_bar = findViewById(R.id.progress_bar);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);


    }

    @Override
    public void onBackPressed() {
        if (postDelay != null) {
            postDelay.cancel();
            postDelay = null;
        }
        stopCountdown();
        removeValue();


    }

    @Override


    protected void onStop() {
        super.onStop();
        //removeValue();

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
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeValue();
        cancelTimer();
        stopCountdown();
    }

    private void cancelTimer() {
        if (count != null) {
            count.cancel();
            count = null;
        }
        if (postDelay != null) {
            postDelay.cancel();
            postDelay = null;
        }


    }

}