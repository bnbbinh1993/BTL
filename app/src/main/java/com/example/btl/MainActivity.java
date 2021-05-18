package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.btl.login.LoginActivity;
import com.example.btl.model.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private MaterialCardView btnTopic;
    private MaterialCardView btnRoom;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private TextView txtName;
    private CircleImageView mAvatar;
    private boolean checkEdit = false;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Profile model = new Profile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGG();
        init();
        getPersonByGG();
        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JoinActivity.class));
            }
        });
        btnTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TopicActivity.class));
            }
        });
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_NoActionBar_Fullscreen);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_profile, viewGroup, false);
                TextView txtN = view1.findViewById(R.id.txtName);
                EditText txtFB = view1.findViewById(R.id.txtFB);
                EditText txtSDT = view1.findViewById(R.id.txtPhone);
                EditText txtEmail = view1.findViewById(R.id.txtGmail);
                ImageButton btnBack = view1.findViewById(R.id.btnBack);
                ImageButton btnMenu = view1.findViewById(R.id.btnMenu);
                LinearLayout btnLogout = view1.findViewById(R.id.btnLogout);
                CircleImageView avt = view1.findViewById(R.id.mAvatar);
                Glide.with(getApplicationContext())
                        .load(acct.getPhotoUrl())
                        .centerCrop()
                        .into(avt);
                txtN.setText(acct.getDisplayName());
                txtEmail.setText(model.getPersonEmail());
                txtFB.setText(model.getPersonFB());
                txtSDT.setText(model.getPersonPhone());

                build.setView(view1);
                AlertDialog dialog = build.create();
                ((ViewGroup) dialog.getWindow().getDecorView())
                        .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                        getApplicationContext(), android.R.anim.slide_in_left));
                dialog.show();
                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOut();
                        revokeAccess();
                        dialog.dismiss();
                    }
                });
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                txtN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtEmail.setFocusableInTouchMode(true);
                        txtEmail.setFocusable(true);
                    }
                });
                btnMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(MainActivity.this, v);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getItemId() == R.id.edit) {
                                    if (item.getTitle().equals("Edit contact")) {
                                        txtEmail.setFocusable(true);
                                        txtFB.setFocusable(true);
                                        txtSDT.setFocusable(true);
                                        txtEmail.setFocusableInTouchMode(true);
                                        txtFB.setFocusableInTouchMode(true);
                                        txtSDT.setFocusableInTouchMode(true);
                                        txtFB.requestFocus();
                                        txtFB.setSelection(txtFB.getText().length());
                                    } else {
                                        txtEmail.setFocusable(false);
                                        txtFB.setFocusable(false);
                                        txtSDT.setFocusable(false);
                                        txtEmail.setFocusableInTouchMode(false);
                                        txtFB.setFocusableInTouchMode(false);
                                        txtSDT.setFocusableInTouchMode(false);
                                        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());

                                        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                        progressDialog.setMessage("Please wait!");
                                        progressDialog.show();

                                        data.child("personFB").setValue(txtFB.getText().toString());
                                        data.child("personPhone").setValue(txtSDT.getText().toString());
                                        data.child("personEmail").setValue(txtEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    return true;
                                }
                                return false;
                            }
                        });
                        popup.inflate(R.menu.menu_edit);
                        if (checkEdit) {
                            popup.getMenu().getItem(0).setTitle("Save");
                            checkEdit = false;
                        } else {
                            popup.getMenu().getItem(0).setTitle("Edit contact");
                            checkEdit = true;
                        }
                        popup.show();
                    }
                });


            }
        });
    }


    private void init() {
        btnTopic = findViewById(R.id.btnTopic);
        btnRoom = findViewById(R.id.btnRoom);
        txtName = findViewById(R.id.txtName);
        mAvatar = findViewById(R.id.mAvatar);

    }


    @SuppressLint("SetTextI18n")
    private void getPersonByGG() {

        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            String a = "<p><b>Hello!   <font color ='red'>" + personName + "</color></b></p>";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtName.setText(Html.fromHtml(a, Html.FROM_HTML_MODE_LEGACY));
            } else {
                txtName.setText(Html.fromHtml((a)));
            }

            Glide.with(this)
                    .load(personPhoto)
                    .centerCrop()
                    .into(mAvatar);

        }


    }

    private void initGG() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (auth != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    model = dataSnapshot.getValue(Profile.class);
                }
            });
        }


    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (acct == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }


}