package com.skai2104.d3srsadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MissingPersonDetailsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mStatusTV;
    private Spinner mStatusSpinner;
    private EditText mNameET, mAgeET, mGenderET, mLocationET, mAttireET, mHeightET, mWeightET, mAddress1ET, mAddress2ET,
            mFacialET, mPhysicalET, mBodyET, mHabitsET, mAdditionalET, mPhoneET, mEmailET;
    private RelativeLayout mSpinnerLayout;
    private CircleImageView mImageIV;
    private LinearLayout mProgressBarLayout;

    private String mName, mAge, mGender, mLocation, mAttire, mHeight, mWeight, mAddress1, mAddress2,
            mFacial, mPhysical, mBody, mHabits, mAdditional, mPhone, mEmail, mStatus, mReportPerson, mDocId, mImage;
    private String mCurrentUserId;
    private boolean mIsEditing = false;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_person_details);

        mStatusTV = findViewById(R.id.statusTV);
        mStatusSpinner = findViewById(R.id.statusSpinner);
        mNameET = findViewById(R.id.nameET);
        mAgeET = findViewById(R.id.ageET);
        mGenderET = findViewById(R.id.genderET);
        mLocationET = findViewById(R.id.locationET);
        mAttireET = findViewById(R.id.attireET);
        mHeightET = findViewById(R.id.heightET);
        mWeightET = findViewById(R.id.weightET);
        mAddress1ET = findViewById(R.id.address1ET);
        mAddress2ET = findViewById(R.id.address2ET);
        mFacialET = findViewById(R.id.facialET);
        mPhysicalET = findViewById(R.id.physicalET);
        mBodyET = findViewById(R.id.bodyET);
        mHabitsET = findViewById(R.id.habitsET);
        mAdditionalET = findViewById(R.id.additionalET);
        mPhoneET = findViewById(R.id.phoneET);
        mEmailET = findViewById(R.id.emailET);
        mSpinnerLayout = findViewById(R.id.spinnerLayout);
        mImageIV = findViewById(R.id.pictureIV);
        mProgressBarLayout = findViewById(R.id.progressBarLayout);

        mProgressBarLayout.setVisibility(View.GONE);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getUid();

        mIsEditing = false;

        mName = getIntent().getStringExtra("name");
        mAge = getIntent().getStringExtra("age");
        mGender = getIntent().getStringExtra("gender");
        mLocation = getIntent().getStringExtra("location");
        mAttire = getIntent().getStringExtra("attire");
        mHeight = getIntent().getStringExtra("height");
        mWeight = getIntent().getStringExtra("weight");
        mAddress1 = getIntent().getStringExtra("address1");
        mAddress2 = getIntent().getStringExtra("address2");
        mFacial = getIntent().getStringExtra("facial");
        mPhysical = getIntent().getStringExtra("physical");
        mBody = getIntent().getStringExtra("body");
        mHabits = getIntent().getStringExtra("habits");
        mAdditional = getIntent().getStringExtra("additional");
        mPhone = getIntent().getStringExtra("phone");
        mEmail = getIntent().getStringExtra("email");
        mStatus = getIntent().getStringExtra("status");
        mReportPerson = getIntent().getStringExtra("reportPerson");
        mDocId = getIntent().getStringExtra("docId");
        mImage = getIntent().getStringExtra("image");

        disableEdit();

        getSupportActionBar().setTitle(mName);

        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.missingStatus, R.layout.missing_status_spinner_item);
        mStatusSpinner.setAdapter(arrayAdapter);

        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mStatusTV.setText(mStatus);
        mNameET.setText(mName);
        mAgeET.setText(mAge);
        mGenderET.setText(mGender);
        mLocationET.setText(mLocation);
        mAttireET.setText(mAttire);
        mHeightET.setText(mHeight);
        mWeightET.setText(mWeight);
        mAddress1ET.setText(mAddress1);
        mAddress2ET.setText(mAddress2);
        mFacialET.setText(mFacial);
        mPhysicalET.setText(mPhysical);
        mBodyET.setText(mBody);
        mHabitsET.setText(mHabits);
        mAdditionalET.setText(mAdditional);
        mPhoneET.setText(mPhone);
        mEmailET.setText(mEmail);

        if (mImage != null) {
            if (!mImage.isEmpty()) {
                Glide.with(this)
                        .load(mImage)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(mImageIV);
            }
        }

        disableEmptyFields();
    }

    private void disableEmptyFields() {
        if (mLocation.isEmpty())
            findViewById(R.id.locationLayout).setVisibility(View.GONE);

        if (mAttire.isEmpty())
            findViewById(R.id.attireLayout).setVisibility(View.GONE);

        if (mAddress1.isEmpty())
            findViewById(R.id.address1Layout).setVisibility(View.GONE);

        if (mAddress2.isEmpty())
            findViewById(R.id.address2Layout).setVisibility(View.GONE);

        if (mFacial.isEmpty())
            findViewById(R.id.facialLayout).setVisibility(View.GONE);

        if (mPhysical.isEmpty())
            findViewById(R.id.physicalLayout).setVisibility(View.GONE);

        if (mBody.isEmpty())
            findViewById(R.id.bodyLayout).setVisibility(View.GONE);

        if (mHabits.isEmpty())
            findViewById(R.id.habitsLayout).setVisibility(View.GONE);

        if (mAdditional.isEmpty())
            findViewById(R.id.additionalLayout).setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mIsEditing) {
            disableEdit();

        } else {
            onBackPressed();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int displayMenu;
        if (!mIsEditing) {
            displayMenu = R.menu.missing_person_details_menu;
        } else {
            displayMenu = R.menu.save_button_menu;
        }
        getMenuInflater().inflate(displayMenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editBtn:
                enableEdit();

                break;

            case R.id.sightingInfoBtn:
                Intent i = new Intent(this, SightingInfoListActivity.class);
                i.putExtra("name", mName);
                i.putExtra("docId", mDocId);
                startActivity(i);

                break;

            case R.id.saveBtn:
                mProgressBarLayout.setVisibility(View.VISIBLE);

                mStatus = String.valueOf(mStatusSpinner.getSelectedItem());

                Map<String, Object> updateStatusMap = new HashMap<>();
                updateStatusMap.put("status", mStatus);

                mFirestore.collection("MissingPersons").document(mDocId).update(updateStatusMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MissingPersonDetailsActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                                mStatusTV.setText(mStatus);
                                mStatusTV.setVisibility(View.VISIBLE);
                                disableEmptyFields();
                                disableEdit();

                                mProgressBarLayout.setVisibility(View.GONE);
                            }
                        });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void disableEdit() {
        mIsEditing = false;
        invalidateOptionsMenu();

        mStatusTV.setVisibility(View.VISIBLE);
        mSpinnerLayout.setVisibility(View.GONE);
        mNameET.setEnabled(false);
        mAgeET.setEnabled(false);
        mGenderET.setEnabled(false);
        mLocationET.setEnabled(false);
        mAttireET.setEnabled(false);
        mHeightET.setEnabled(false);
        mWeightET.setEnabled(false);
        mAddress1ET.setEnabled(false);
        mAddress2ET.setEnabled(false);
        mFacialET.setEnabled(false);
        mPhysicalET.setEnabled(false);
        mBodyET.setEnabled(false);
        mHabitsET.setEnabled(false);
        mAdditionalET.setEnabled(false);
        mPhoneET.setEnabled(false);
        mEmailET.setEnabled(false);
    }

    public void enableEdit() {
        mIsEditing = true;
        invalidateOptionsMenu();

        mStatusTV.setVisibility(View.GONE);
        mSpinnerLayout.setVisibility(View.VISIBLE);

        if (mStatus.equals("Found"))
            mStatusSpinner.setSelection(0);
        else
            mStatusSpinner.setSelection(1);
    }
}
