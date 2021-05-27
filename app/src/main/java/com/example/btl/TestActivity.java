package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.btl.adapter.AdapterUser;
import com.example.btl.adapter.RankAdapter;
import com.example.btl.adapter.RankTestAdapter;
import com.example.btl.model.Point;
import com.example.btl.model.Profile;
import com.example.btl.model.Topic;
import com.example.btl.model.User;
import com.example.btl.utils.ClickItem;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TestActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialButton btnNext;
    private MaterialButton btnFinish_end;
    private Topic model = new Topic();
    private int position = 1;
    private int size = 0;
    private long keyTime = 1000;
    private TextView txtQuestion, txtAnswerA, txtAnswerB, txtAnswerC, txtAnswerD, txtTotal, txtTime;
    private LinearLayout layoutA, layoutB, layoutC, layoutD;
    private CountDownTimer downTimer;
    private CountDownTimer postDelay;
    private int point = 0;
    private int answer = 0;
    private RelativeLayout end_test;
    private RelativeLayout layoutTest;
    private String id_topic = "1000";
    private ProgressBar progress_bar;
    private TextView txtPoint;
    private TextView txtRank;
    private LinearLayout layoutRank;
    private List<Point> scoreList;
    private RecyclerView mRank;
    private RankTestAdapter rankAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
        initUtils();
        actionClick();
    }

    private void initUtils() {
        scoreList = new ArrayList<>();
        end_test.setVisibility(View.GONE);
        layoutTest.setVisibility(View.VISIBLE);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            id_topic = intent.getStringExtra("_topic_id");
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

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                if (postDelay != null) {
                    postDelay.cancel();
                    postDelay = null;
                }
                if (position > size) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("topic").child(id_topic).child("rank");
                    Point map = new Point(user.getUid(), user.getDisplayName(), String.valueOf(point), String.valueOf(user.getPhotoUrl()));
                    reference.child(user.getUid()).setValue(map);
                    txtPoint.setText("Score: " + point);
                    btnNext.setVisibility(View.GONE);
                    end_test.setVisibility(View.VISIBLE);
                    layoutTest.setVisibility(View.GONE);
                    isRank();
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
        }.start();


    }


    private void isRank() {
        layoutRank.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.INVISIBLE);
        scoreList = new ArrayList<>();
        rankAdapter = new RankTestAdapter(scoreList);
        mRank.setHasFixedSize(true);
        mRank.setLayoutManager(new GridLayoutManager(TestActivity.this, 1));
        mRank.setAdapter(rankAdapter);

        rankAdapter.setOnClickItem(new ClickItem() {
            @Override
            public void click(int position) {
                viewInfor((position));
            }
        });

        DatabaseReference rank = FirebaseDatabase.getInstance().getReference().child("topic").child(id_topic).child("rank");
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
                rankAdapter.notifyDataSetChanged();
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

    @SuppressLint("ObsoleteSdkInt")
    private void copy(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);

        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);

        }
        Toast.makeText(context, "Copy successful!", Toast.LENGTH_SHORT).show();
    }


    private void viewInfor(int position) {

        if (!scoreList.get(position).getUid().equals(user.getUid())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
            View view = LayoutInflater.from(TestActivity.this).inflate(R.layout.show_user_dialog, null);
            TextView txtN = view.findViewById(R.id.txtN);
            TextView txtFB = view.findViewById(R.id.txtFB);
            TextView txtEmail = view.findViewById(R.id.txtEmail);
            TextView txtSDT = view.findViewById(R.id.txtSDT);
            CircleImageView mAvatar = view.findViewById(R.id.mAvatar);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(scoreList.get(position).getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    Profile md = dataSnapshot.getValue(Profile.class);
                    assert md != null;
                    txtN.setText(md.getPersonName());
                    txtFB.setText(md.getPersonFB());
                    txtEmail.setText(md.getPersonEmail());
                    txtSDT.setText(md.getPersonPhone());
                    Glide.with(TestActivity.this)
                            .load(md.getPersonPhoto())
                            .centerCrop()
                            .into(mAvatar);
                }

            });

            txtN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(TestActivity.this, txtN.getText().toString());
                }
            });
            txtFB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(TestActivity.this, txtFB.getText().toString());
                }
            });
            txtEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(TestActivity.this, txtEmail.getText().toString());
                }
            });
            txtSDT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(TestActivity.this, txtSDT.getText().toString());
                }
            });
        }

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
        answer = 0;
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
                answer = 1;
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
                answer = 2;
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
                answer = 3;
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
                answer = 4;
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

        btnFinish_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void checkChoose(int i) {
        if (model.getQuestion().get(position).getAnswerTrue() != answer) {
            switch (answer) {
                case 1: {
                    layoutA.setBackgroundResource(R.color.btnChoose);
                    break;
                }
                case 2: {
                    layoutB.setBackgroundResource(R.color.btnChoose);
                    break;
                }
                case 3: {
                    layoutC.setBackgroundResource(R.color.btnChoose);
                    break;
                }
                case 4: {
                    layoutD.setBackgroundResource(R.color.btnChoose);
                    break;
                }
                default: {
                    // k làm gì ^^
                }

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void checkAnwser() {
        if (model.getQuestion().get(position).getAnswerTrue() == answer) {
            point = point + 100;
        }

        switch (model.getQuestion().get(position).getAnswerTrue()) {
            case 1: {
                layoutA.setBackgroundResource(R.color.correct);
                layoutB.setBackgroundResource(R.color.wrong);
                layoutC.setBackgroundResource(R.color.wrong);
                layoutD.setBackgroundResource(R.color.wrong);
                checkChoose(1);
                break;
            }
            case 2: {

                layoutA.setBackgroundResource(R.color.wrong);
                layoutB.setBackgroundResource(R.color.correct);
                layoutC.setBackgroundResource(R.color.wrong);
                layoutD.setBackgroundResource(R.color.wrong);
                checkChoose(2);
                break;
            }
            case 3: {

                layoutA.setBackgroundResource(R.color.wrong);
                layoutB.setBackgroundResource(R.color.wrong);
                layoutC.setBackgroundResource(R.color.correct);
                layoutD.setBackgroundResource(R.color.wrong);
                checkChoose(3);
                break;
            }
            case 4: {
                layoutA.setBackgroundResource(R.color.wrong);
                layoutB.setBackgroundResource(R.color.wrong);
                layoutC.setBackgroundResource(R.color.wrong);
                layoutD.setBackgroundResource(R.color.correct);
                checkChoose(4);
                break;
            }
        }
        answer = 0;
        position++;
    }


    private void init() {

        btnNext = findViewById(R.id.btnNext);
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
        end_test = findViewById(R.id.end_test);
        layoutTest = findViewById(R.id.layoutTest);
        btnFinish_end = findViewById(R.id.btnFinish_end);
        progress_bar = findViewById(R.id.progress_bar);
        txtPoint = findViewById(R.id.txtPoint);
        txtRank = findViewById(R.id.txtRank);
        layoutRank = findViewById(R.id.layoutRank);
        progress_bar = findViewById(R.id.progress_bar);
        mRank = findViewById(R.id.mRank);


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