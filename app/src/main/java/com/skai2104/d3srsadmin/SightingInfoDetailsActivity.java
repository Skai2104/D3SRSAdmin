package com.skai2104.d3srsadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SightingInfoDetailsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mMissingPersonNameTV, mDateTimeTV, mReportedTV;
    private EditText mContentET, mLocationET;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    private String mCurrentUserId;
    private String mDateTime, mContent, mReportPersonName, mReportPersonId, mDocId, mLocation, mMissingPersonName, mMissingPersonId;
    private boolean mIsEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sighting_info_details);

        mMissingPersonNameTV = findViewById(R.id.nameTV);
        mDateTimeTV = findViewById(R.id.dateTimeTV);
        mReportedTV = findViewById(R.id.reportedTV);
        mContentET = findViewById(R.id.contentET);
        mLocationET = findViewById(R.id.locationET);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sighting Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getUid();

        mIsEditing = false;

        mDateTime = getIntent().getStringExtra("datetime");
        mContent = getIntent().getStringExtra("content");
        mReportPersonName = getIntent().getStringExtra("reportPersonName");
        mReportPersonId = getIntent().getStringExtra("reportPersonId");
        mDocId = getIntent().getStringExtra("docId");
        mLocation = getIntent().getStringExtra("location");
        mMissingPersonName = getIntent().getStringExtra("name");
        mMissingPersonId = getIntent().getStringExtra("id");

        mMissingPersonNameTV.setText(mMissingPersonName);
        mDateTimeTV.setText(mDateTime);
        mContentET.setText(mContent);
        mLocationET.setText(mLocation);

        mReportedTV.setText(mReportPersonName);

        disableEdit();

        mLocationET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        mContentET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    public void disableEdit() {
        mIsEditing = false;
        invalidateOptionsMenu();

        mContentET.setEnabled(false);
        mLocationET.setEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
    }
}
