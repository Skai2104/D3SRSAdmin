package com.skai2104.d3srsadmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText mEmailET, mPasswordET;
    private LinearLayout mProgressBarLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String mTokenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailET = findViewById(R.id.emailET);
        mPasswordET = findViewById(R.id.passwordET);
        mProgressBarLayout = findViewById(R.id.progressBarLayout);
        mProgressBarLayout.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });
    }

    public void login() {
        String email = mEmailET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();

        if (!hasValidationError(email, password)) {
            mProgressBarLayout.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        mTokenId = instanceIdResult.getToken();
                                        String currentId = mAuth.getCurrentUser().getUid();

                                        Map<String, Object> updateInfoMap = new HashMap<>();
                                        updateInfoMap.put("token_id", mTokenId);

                                        mFirestore.collection("Authorities").document(currentId).update(updateInfoMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    mProgressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Invalid user", Toast.LENGTH_SHORT).show();

                                } else {
                                    mProgressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }

    public boolean hasValidationError(String email, String password) {
        if (email.isEmpty()) {
            mEmailET.setError("Email is required");
            mEmailET.requestFocus();
            return true;
        }

        if (password.isEmpty()) {
            mPasswordET.setError("Password is required");
            mPasswordET.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent i = new Intent(this, DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }
}
