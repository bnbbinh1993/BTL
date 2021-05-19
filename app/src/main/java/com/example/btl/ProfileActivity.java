package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.btl.login.LoginActivity;
import com.example.btl.model.Profile;
import com.example.btl.utils.Pef;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView txtN;
    EditText txtFB;
    EditText txtSDT;
    EditText txtEmail;
    ImageButton btnBack;
    ImageButton btnMenu;
    LinearLayout btnLogout;
    CircleImageView avt;
    private Profile model;
    private GoogleSignInAccount acct;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean checkEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        initUtils();
    }

    private void init() {
        txtN = findViewById(R.id.txtName);
        txtFB = findViewById(R.id.txtFB);
        txtSDT = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtGmail);
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);
        btnLogout = findViewById(R.id.btnLogout);
        avt = findViewById(R.id.mAvatar);
    }

    private void initUtils() {

        Pef.getReference(ProfileActivity.this);
        Pef.setFullScreen();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(Profile.class);
                if (model != null) {
                    Glide.with(getApplicationContext())
                            .load(acct.getPhotoUrl())
                            .centerCrop()
                            .into(avt);
                    txtN.setText(acct.getDisplayName());
                    txtEmail.setText(model.getPersonEmail());
                    txtFB.setText(model.getPersonFB());
                    txtSDT.setText(model.getPersonPhone());
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                revokeAccess();

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.out_left, R.anim.in_left);
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
                PopupMenu popup = new PopupMenu(ProfileActivity.this, v);
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
                                checkEdit = true;
                            } else {
                                txtEmail.setFocusable(false);
                                txtFB.setFocusable(false);
                                txtSDT.setFocusable(false);
                                txtEmail.setFocusableInTouchMode(false);
                                txtFB.setFocusableInTouchMode(false);
                                txtSDT.setFocusableInTouchMode(false);
                                DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
                                ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                                progressDialog.setMessage("Please wait!");
                                progressDialog.show();

                                if (txtFB.getText().toString().isEmpty() || txtSDT.getText().toString().isEmpty() || txtEmail.getText().toString().isEmpty()) {
                                    Toast.makeText(ProfileActivity.this, "Can not be empty!", Toast.LENGTH_SHORT).show();
                                } else {
                                    data.child("personFB").setValue(txtFB.getText().toString());
                                    data.child("personPhone").setValue(txtSDT.getText().toString());
                                    data.child("personEmail").setValue(txtEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                    checkEdit = false;
                                }


                            }

                            return true;
                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.menu_edit);
                if (checkEdit) {
                    popup.getMenu().getItem(0).setTitle("Save");

                } else {
                    popup.getMenu().getItem(0).setTitle("Edit contact");

                }
                popup.show();
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.bottom_out, R.anim.bottom_in);
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
    public void onBackPressed() {

        overridePendingTransition(R.anim.out_left, R.anim.in_left);
        finish();
    }
}