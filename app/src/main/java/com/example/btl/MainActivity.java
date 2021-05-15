package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.btl.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;

import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private MaterialCardView btnTopic;
    private MaterialCardView btnRoom;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private TextView txtName;
    private CircleImageView mAvatar;



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
                signOut();
                revokeAccess();
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
                txtName.setText(Html.fromHtml(a));
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
        FirebaseAuth auth = FirebaseAuth.getInstance();

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