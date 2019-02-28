package com.skai2104.d3srsadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class UserRegistrationActivity extends AppCompatActivity {
    private EditText mNameET, mEmailET, mPasswordET, mPhoneET;
    private ProgressBar mProgressBar;

    private String mName, mEmail, mPhone;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mNameET = findViewById(R.id.nameET);
        mEmailET = findViewById(R.id.emailET);
        mPasswordET = findViewById(R.id.passwordET);
        mPhoneET = findViewById(R.id.phoneET);
        mProgressBar = findViewById(R.id.progressBar);

        mProgressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SignUpActivity.this, String.valueOf(mLatitude) + "," + String.valueOf(mLongitude), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(UserRegistrationActivity.this);
                builder.setTitle("User Registration")
                        .setMessage("Confirm user registration?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                register();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    public void register() {
        mName = mNameET.getText().toString().trim();
        mEmail = mEmailET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();
        mPhone = mPhoneET.getText().toString().trim();

        if (!hasValidationError(mName, mEmail, password, mPhone)) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(mEmail, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = task.getResult().getUser().getUid();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", mName);
                                userMap.put("email", mEmail);
                                userMap.put("phone", mPhone);

                                mFirestore.collection("Authorities").document(userId).set(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UserRegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                                finish();
                                            }
                                        });
                            } else {
                                Toast.makeText(UserRegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public boolean hasValidationError(String name, String email, String password, String phone) {
        if (name.isEmpty()) {
            mNameET.setError("Name required");
            mNameET.requestFocus();
            return true;
        }

        if (email.isEmpty()) {
            mEmailET.setError("Email required");
            mEmailET.requestFocus();
            return true;
        }

        if (password.isEmpty()) {
            mPasswordET.setError("Password required");
            mPasswordET.requestFocus();
            return true;
        }

        if (phone.isEmpty()) {
            mPhoneET.setError("Phone required");
            mPhoneET.requestFocus();
            return true;
        }
        return false;
    }
}
