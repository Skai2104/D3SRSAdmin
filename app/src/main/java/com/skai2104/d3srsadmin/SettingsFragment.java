package com.skai2104.d3srsadmin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class SettingsFragment extends Fragment {
    private Button mRegistrationBtn;

    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mRegistrationBtn = view.findViewById(R.id.registrationBtn);
        mRegistrationBtn.setVisibility(View.GONE);
        mRegistrationBtn.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        if (mCurrentUserId.equals("BT6IgnwN2fPBagmmKJJ15ds9EgS2")) {
            mRegistrationBtn.setVisibility(View.VISIBLE);
            mRegistrationBtn.setEnabled(true);
        }

        view.findViewById(R.id.myAccountBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AccountSettingsActivity.class);
                startActivity(i);
            }
        });

        mRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), UserRegistrationActivity.class);
                startActivity(i);
            }
        });

        return view;
    }
}
