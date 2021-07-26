package com.example.maps;//package com.example.maps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_INTERVAL = 5000;
    private static final float METERS_TO_MILES_CONVERSION = 0.000621371F;

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    Polyline mPolyline;
    List<LatLng> mCoords;
    float mDistanceTraveled;
    Long mLastLocationTime;

    private TextView txtDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setTitle("Map Location Activity");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        mCoords = new ArrayList<LatLng>();
        mDistanceTraveled = 0F;
        mLastLocationTime = 0L;

        txtDistance = findViewById(R.id.txtDistance);
    }

    // Activities should strongly consider removing all location request when entering the background
    // (for example at Activity.onPause()), or at least swap the request to a larger interval and lower quality.
    // For background use cases, the PendingIntent version of the
    // requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()) method
    // is recommended: requestLocationUpdates(LocationRequest, PendingIntent).
    @Override
    public void onPause() {
        super.onPause();

        // Stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL); // 5 seconds
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mPolyline = mGoogleMap.addPolyline(new PolylineOptions());

        // Initialize Google Play Services
        // Check if device's SDK version is Android 6.0 Marshmallow or newer
        // This is because as of 6.0, dangerous permissions are no longer granted at install time,
        // but must be requested by the application at runtime through Activity.requestPermissions(String[], int)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                // Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
            mLastLocation = location;

            /*
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            */

            // Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mCoords.add(latLng);

            if (mCoords.size() > 1) {
                // getSpeed() has units 'm/s'
                mDistanceTraveled += location.getSpeed() * (location.getTime() - mLastLocationTime) / 1000.0;
                txtDistance.setText(String.format("%.1f", METERS_TO_MILES_CONVERSION * mDistanceTraveled));
            }

            mLastLocationTime = location.getTime();

            mPolyline.setPoints(mCoords);

            /*
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
             */

            if (mCoords.size() == 1) {
                // Move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            } else {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            // If it's the first time the app has been launched
            // OR the user has asked not to see the request again, do not show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Prompt the user once explanation has been shown
                                // The result will be received in the onRequestPermissionsResult(int requestCode,
                                // String permissions[], int[] grantResults)
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                // The result will be received in the onRequestPermissionsResult(int requestCode,
                // String permissions[], int[] grantResults)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

//    If your app does not have the requested permissions the user will be presented with UI for accepting them.
//    After the user has accepted or rejected the requested permissions you will receive a callback
//    reporting whether the permissions were granted or not. Your activity has to implement
//    ActivityCompat.OnRequestPermissionsResultCallback and the results of permission requests will be delivered to
//    its ActivityCompat.OnRequestPermissionsResultCallback.onRequestPermissionsResult(int, String[], int[]) method.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted!
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission was denied. Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // Other 'case' lines to check for other permissions this app might request
        }
    }

    private double calculateDistanceTraveled(List<LatLng> mCoords) {
        if (mCoords.size() > 1) {
            LatLng start;
            LatLng end;
            double latTotalDelta = 0;
            double longTotalDelta = 0;

            for (int i = 1; i < mCoords.size(); i++) {
                start = mCoords.get(i - 1);
                end = mCoords.get(i);
                latTotalDelta += Math.abs(end.latitude - start.latitude);
                longTotalDelta += Math.abs(end.longitude - start.longitude);
            }

            return Math.sqrt(latTotalDelta * latTotalDelta + longTotalDelta * longTotalDelta);
        }

        return 0;
    }
}