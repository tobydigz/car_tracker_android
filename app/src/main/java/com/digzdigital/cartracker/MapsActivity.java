package com.digzdigital.cartracker;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Marker carMarker;
    private Fragment loginFragment, mapFragment;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 0;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("devices").child(getIntent().getStringExtra("deviceId"));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng lagos = new LatLng(6.6, 3.3);
        // map.addMarker(new MarkerOptions().position(lagos).title("Marker in Sydney"));


        map.moveCamera(CameraUpdateFactory.newLatLng(lagos));
        determineCarLocation();
    }

    private void determineCarLocation(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double latitude = null, longitude = null;
                try{
                    latitude = (Double) dataSnapshot.child("latitude").getValue();
                    longitude = (Double) dataSnapshot.child("longitude").getValue();
                }catch (ClassCastException e){
                    latitude = Double.valueOf((String) dataSnapshot.child("latitude").getValue());
                    longitude = Double.valueOf((String) dataSnapshot.child("longitude").getValue());
                }

                if (longitude!=null&&latitude!=null)updateMarker(latitude, longitude);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateMarker(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude,longitude);
        getcarMarker().setPosition(latLng);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    private Marker getcarMarker() {
        if (carMarker == null)  carMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(0,0))
                .title("Car Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        return carMarker;
    }


}
