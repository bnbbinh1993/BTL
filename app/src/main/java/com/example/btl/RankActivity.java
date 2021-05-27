package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.btl.adapter.RankAdapter;
import com.example.btl.model.Point;
import com.example.btl.model.Profile;
import com.example.btl.utils.ClickItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
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

public class RankActivity extends AppCompatActivity {
    private List<Point> points;
    private List<Point> points2;

    private RankAdapter rankAdapter;
    private RecyclerView mRank;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialToolbar toolbar;

    private CircleImageView mAvatar1;
    private CircleImageView mAvatar2;
    private CircleImageView mAvatar3;

    private TextView txtName1;
    private TextView txtName2;
    private TextView txtName3;

    private TextView txtPoint1;
    private TextView txtPoint2;
    private TextView txtPoint3;

    private LinearLayout view1, view2, view3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        points = new ArrayList<>();
        points2 = new ArrayList<>();

        rankAdapter = new RankAdapter(points);
        mRank.setHasFixedSize(true);
        mRank.setLayoutManager(new GridLayoutManager(RankActivity.this, 1));
        mRank.setAdapter(rankAdapter);
        rankAdapter.setOnClickItem(new ClickItem() {
            @Override
            public void click(int position) {
                viewInfor((position + 3));

            }

        });

        DatabaseReference rank = FirebaseDatabase.getInstance().getReference().child("room").child(RoomActivity.id_room).child("coin");
        rank.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                points.clear();
                points2.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Point md = ds.getValue(Point.class);
                    points.add(md);
                    points2.add(md);

                }
                Collections.sort(points);
                Collections.sort(points2);
                if (points.size() >= 3) {
                    Glide.with(getApplicationContext())
                            .load(points.get(0).getUrl())
                            .into(mAvatar1);
                    Glide.with(getApplicationContext())
                            .load(points.get(1).getUrl())
                            .into(mAvatar2);
                    Glide.with(getApplicationContext())
                            .load(points.get(2).getUrl())
                            .into(mAvatar3);
                    txtName1.setText(points.get(0).getName());
                    txtName2.setText(points.get(1).getName());
                    txtName3.setText(points.get(2).getName());
                    txtPoint1.setText(points.get(0).getScore());
                    txtPoint2.setText(points.get(1).getScore());
                    txtPoint3.setText(points.get(2).getScore());
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.VISIBLE);
                } else if (points.size() >= 2) {
                    Glide.with(getApplicationContext())
                            .load(points.get(0).getUrl())
                            .into(mAvatar1);
                    Glide.with(getApplicationContext())
                            .load(points.get(1).getUrl())
                            .into(mAvatar2);

                    txtName1.setText(points.get(0).getName());
                    txtName2.setText(points.get(1).getName());
                    txtPoint1.setText(points.get(0).getScore());
                    txtPoint2.setText(points.get(1).getScore());
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.INVISIBLE);

                } else if (points.size() >= 1) {
                    Glide.with(getApplicationContext())
                            .load(points.get(0).getUrl())
                            .into(mAvatar1);
                    txtName1.setText(points.get(0).getName());
                    txtPoint1.setText(points.get(0).getScore());
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.INVISIBLE);
                    view3.setVisibility(View.INVISIBLE);
                } else {
                    view1.setVisibility(View.INVISIBLE);
                    view2.setVisibility(View.INVISIBLE);
                    view3.setVisibility(View.INVISIBLE);

                }


                if (points.size() >= 3) {
                    points.remove(points.get(2));
                    points.remove(points.get(1));
                    points.remove(points.get(0));

                } else {
                    points.removeAll(points);
                }


                rankAdapter.notifyDataSetChanged();
//                for (int i = 0; i < points.size(); i++) {
//                    if (user.getUid().equals(points.get(i).getUid())) {
//                        //txtRank.setText("Ranking: " + (i + 1));
//                        //break;
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mAvatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewInfor(0);
            }
        });
        mAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewInfor(1);
            }
        });
        mAvatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewInfor(2);
            }
        });

    }

    private void viewInfor(int position) {
        if (!points2.get(position).getUid().equals(user.getUid())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RankActivity.this);
            View view = LayoutInflater.from(RankActivity.this).inflate(R.layout.show_user_dialog, null);
            TextView txtN = view.findViewById(R.id.txtN);
            TextView txtFB = view.findViewById(R.id.txtFB);
            TextView txtEmail = view.findViewById(R.id.txtEmail);
            TextView txtSDT = view.findViewById(R.id.txtSDT);
            CircleImageView mAvatar = view.findViewById(R.id.mAvatar);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(points2.get(position).getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    Profile md = dataSnapshot.getValue(Profile.class);
                    assert md != null;
                    txtN.setText(md.getPersonName());
                    txtFB.setText(md.getPersonFB());
                    txtEmail.setText(md.getPersonEmail());
                    txtSDT.setText(md.getPersonPhone());
                    Glide.with(RankActivity.this)
                            .load(md.getPersonPhoto())
                            .centerCrop()
                            .into(mAvatar);
                }

            });

            txtN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(RankActivity.this, txtN.getText().toString());
                }
            });
            txtFB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(RankActivity.this, txtFB.getText().toString());
                }
            });
            txtEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(RankActivity.this, txtEmail.getText().toString());
                }
            });
            txtSDT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(RankActivity.this, txtSDT.getText().toString());
                }
            });
        }
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

    private void init() {
        mRank = findViewById(R.id.mRank);
        toolbar = findViewById(R.id.toolbar);
        mAvatar1 = findViewById(R.id.mAvatar1);
        mAvatar2 = findViewById(R.id.mAvatar2);
        mAvatar3 = findViewById(R.id.mAvatar3);
        txtName1 = findViewById(R.id.txtName1);
        txtName2 = findViewById(R.id.txtName2);
        txtName3 = findViewById(R.id.txtName3);
        txtPoint1 = findViewById(R.id.txtPoint1);
        txtPoint2 = findViewById(R.id.txtPoint2);
        txtPoint3 = findViewById(R.id.txtPoint3);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}