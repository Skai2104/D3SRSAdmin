package com.skai2104.d3srsadmin;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private RecyclerView mSosListRV;
    private TextView mNoSosTV, mListTV, mMapTV;
    private RelativeLayout mMapViewLayout;
    private MapView mMapView;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    private Geocoder mGeocoder;
    private Address mReturnedAddress;
    private Bundle mSavedInstanceState;

    private GoogleMap mMap;

    private double mLatitude = 0.0, mLongitude = 0.0;
    private StringBuilder mReturnedAddressStr;
    private String mAddress;
    private List<Address> mAddressList;
    private List<SOS> mSosList;
    private SOSListRecyclerAdapter mAdapter;
    private boolean mHasData = false, mIsLoaded = false;

    private GeoApiContext mGeoApiContext = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mSosListRV = view.findViewById(R.id.sosListRV);
        mNoSosTV = view.findViewById(R.id.noSosTV);
        mListTV = view.findViewById(R.id.listTV);
        mMapTV = view.findViewById(R.id.mapTV);
        mMapViewLayout = view.findViewById(R.id.mapViewLayout);
        mMapView = view.findViewById(R.id.mapView);

        mSosListRV.setVisibility(View.GONE);
        mNoSosTV.setVisibility(View.VISIBLE);

        mReturnedAddressStr = new StringBuilder();
        mSosList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAdapter = new SOSListRecyclerAdapter(getContext(), mSosList);

        mSosListRV.setHasFixedSize(true);
        mSosListRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mSosListRV.setAdapter(mAdapter);

        mSavedInstanceState = savedInstanceState;

        initGoogleMap(mSavedInstanceState);

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng location;
        float zoomLevel = 11.5f;

        mMap = map;

        if (mIsLoaded) {
            MarkerOptions markerOptions = new MarkerOptions();

            for (SOS sos : mSosList) {
                location = new LatLng(Double.valueOf(sos.getLatitude()), Double.valueOf(sos.getLongitude()));

                markerOptions.position(location);
                markerOptions.title("Location of " + sos.getFrom());

                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
            }
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();

        mFirebaseUser = mAuth.getCurrentUser();

        // Get the full address from the latitude and longitude
        mGeocoder = new Geocoder(getContext(), Locale.getDefault());
        mAddressList = new ArrayList<>();
        mAddress = "";

        if (mFirebaseUser != null) {
            mSosList.clear();

            mFirestore.collection("SOS").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            String docId = doc.getDocument().getId();

                            mReturnedAddressStr = new StringBuilder();

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                SOS sos = doc.getDocument().toObject(SOS.class);

                                try {
                                    mAddressList = mGeocoder.getFromLocation(Double.valueOf(sos.getLatitude()), Double.valueOf(sos.getLongitude()), 1);
                                    if (!mAddressList.isEmpty()) {
                                        mReturnedAddress = mAddressList.get(0);

                                        for (int i = 0; i <= mReturnedAddress.getMaxAddressLineIndex(); i++) {
                                            mReturnedAddressStr.append(mReturnedAddress.getAddressLine(i)).append("\n");
                                        }
                                        mAddress = mReturnedAddressStr.toString();

                                    } else {
                                        Log.d("Current address error", "No address returned");
                                    }
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                                sos.setAddress(mAddress);
                                sos.setDocId(docId);
                                mSosList.add(sos);

                                mAdapter.notifyDataSetChanged();
                            }
                        }
                        mListTV.setBackgroundResource(R.drawable.left_btn_border_filled);
                        mMapTV.setBackgroundResource(R.drawable.right_btn_border);

                        mHasData = false;
                        if (!mSosList.isEmpty()) {
                            mSosListRV.setVisibility(View.VISIBLE);
                            mNoSosTV.setVisibility(View.GONE);
                            mMapViewLayout.setVisibility(View.GONE);
                            mHasData = true;

                        } else {
                            mSosListRV.setVisibility(View.GONE);
                            mNoSosTV.setVisibility(View.VISIBLE);
                            mMapViewLayout.setVisibility(View.GONE);
                            mHasData = false;
                        }

                        mListTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListTV.setBackgroundResource(R.drawable.left_btn_border_filled);
                                mMapTV.setBackgroundResource(R.drawable.right_btn_border);

                                if (mHasData) {
                                    mSosListRV.setVisibility(View.VISIBLE);
                                    mNoSosTV.setVisibility(View.GONE);
                                    mMapViewLayout.setVisibility(View.GONE);
                                } else {
                                    mSosListRV.setVisibility(View.GONE);
                                    mNoSosTV.setVisibility(View.VISIBLE);
                                    mMapViewLayout.setVisibility(View.GONE);
                                }
                            }
                        });

                        mMapTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListTV.setBackgroundResource(R.drawable.left_btn_border);
                                mMapTV.setBackgroundResource(R.drawable.right_btn_border_filled);

                                if (mHasData) {
                                    mSosListRV.setVisibility(View.GONE);
                                    mNoSosTV.setVisibility(View.GONE);
                                    mMapViewLayout.setVisibility(View.VISIBLE);

                                } else {
                                    mSosListRV.setVisibility(View.GONE);
                                    mNoSosTV.setVisibility(View.VISIBLE);
                                    mMapViewLayout.setVisibility(View.GONE);
                                }

                                mIsLoaded = true;
                                mMapView.getMapAsync(HomeFragment.this);
                            }
                        });
                    }
                }
            });
        }
    }
}
