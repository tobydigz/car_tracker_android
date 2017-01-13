package com.digzdigital.cartracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PointOfInterest;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    private GoogleMap map;
    // private LatLngBounds COVENANT_UNIVERSITY = new LatLngBounds(new LatLng(6.678873, 3.162612), new LatLng(6.667876, 3.151196));
    private LatLngBounds COVENANT_UNIVERSITY = new LatLngBounds(new LatLng(6.667876, 3.151196), new LatLng(6.678873, 3.162612));
    private UiSettings uiSettings;
    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;
    private boolean locationPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        uiSettings = map.getUiSettings();

        LatLng lagos = new LatLng(6.670306, 3.158380);
        // map.addMarker(new MarkerOptions().position(lagos).title("Marker in Sydney"));


        map.moveCamera(CameraUpdateFactory.newLatLng(lagos));

        setMyLocationButtonEnabled();
        setMyLocationLayerEnabled();
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(COVENANT_UNIVERSITY, 0));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(COVENANT_UNIVERSITY.getCenter(), 16));
                map.setLatLngBoundsForCameraTarget(COVENANT_UNIVERSITY);
            }
        });
    }


    public void setMyLocationButtonEnabled() {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location button (this DOES NOT enable/disable the my location
        // dot/chevron on the map). The my location button will never appear if the my location
        // layer is not enabled.
        // First verify that the location permission has been granted.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            uiSettings.setMyLocationButtonEnabled(true);
        } else {
            // Uncheck the box and request missing location permission.
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkReady() {
        if (map == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setMyLocationLayerEnabled() {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location layer (i.e., the dot/chevron on the map). If enabled, it
        // will also cause the my location button to show (if it is enabled); if disabled, the my
        // location button will never show.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Uncheck the box and request missing location permission.
            PermissionUtils.requestPermission(this, LOCATION_LAYER_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(
                    getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            // Enable the My Location button if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                uiSettings.setMyLocationButtonEnabled(true);
            } else {
                locationPermissionDenied = true;
            }

        } else if (requestCode == LOCATION_LAYER_PERMISSION_REQUEST_CODE) {
            // Enable the My Location layer if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                setMyLocationLayerEnabled();
            } else {
                locationPermissionDenied = true;
            }
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (locationPermissionDenied) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(false).show(getSupportFragmentManager(), "dialog");
            locationPermissionDenied = false;
        }
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        String latitude = String.valueOf(pointOfInterest.latLng.latitude);
        String longitude = String.valueOf(pointOfInterest.latLng.longitude);
        Uri uri = Uri.parse("google.navigation:q="+ latitude + ","+ longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
}
