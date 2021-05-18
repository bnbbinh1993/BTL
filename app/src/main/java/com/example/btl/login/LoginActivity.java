package com.example.btl.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.btl.MainActivity;
import com.example.btl.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 100;
    private FacebookSdk facebookSdk;
    private AppEventsLogger appEventsLogger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        auth = FirebaseAuth.getInstance();
        ImageButton gg = findViewById(R.id.imageTop);
        gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount inAccount = accountTask.getResult(ApiException.class);
                if (inAccount != null) {
                    firebaseAuth(inAccount);
                }


            } catch (Exception e) {
                Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuth(GoogleSignInAccount inAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(inAccount.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference checkAccount = FirebaseDatabase.getInstance().getReference().child("User");
                    checkAccount.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (dataSnapshot.hasChild(Objects.requireNonNull(user).getUid())) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                UpdateData(inAccount);
                            }
                        }
                    });

                }
            }
        });
    }

    private void UpdateData(GoogleSignInAccount inAccount) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Map<String, String> map = new HashMap<>();
        map.put("personName", inAccount.getDisplayName());
        map.put("personGivenName", inAccount.getGivenName());
        map.put("personFamilyName", inAccount.getFamilyName());
        map.put("personEmail", inAccount.getEmail());
        map.put("personId", inAccount.getId());
        map.put("personPhoto", String.valueOf(inAccount.getPhotoUrl()));
        map.put("personPhone", "0");
        map.put("personFB", "null");
        reference.child("User").child(Objects.requireNonNull(user).getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.dismiss();
            }
        });


    }

    private void test() {

//        if (inAccount.getPhotoUrl() != null) {
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Upload Photos").child(System.currentTimeMillis() +
//                    "." + inAccount.getPhotoUrl());
//            storageReference.putFile(inAccount.getPhotoUrl())
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            Map<String, String> map = new HashMap<>();
//
//                            map.put("personName", inAccount.getDisplayName());
//                            map.put("personGivenName", inAccount.getGivenName());
//                            map.put("personFamilyName", inAccount.getFamilyName());
//                            map.put("personEmail", inAccount.getEmail());
//                            map.put("personId", inAccount.getId());
//                            map.put("URL", taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//
//                            reference.child("User").child(user.toString()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(LoginActivity.this, "onSuccess!", Toast.LENGTH_SHORT).show();
//                                    progressDialog.setCanceledOnTouchOutside(false);
//                                    progressDialog.dismiss();
//                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                    finish();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(LoginActivity.this, "onFailure!", Toast.LENGTH_SHORT).show();
//                                    progressDialog.setCanceledOnTouchOutside(false);
//                                    progressDialog.dismiss();
//                                }
//                            });
//
//
//                        }
//                    });

    }
}


