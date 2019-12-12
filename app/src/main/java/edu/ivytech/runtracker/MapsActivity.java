package edu.ivytech.runtracker;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.ivytech.runtracker.database.RunTrackerDB;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnSuccessListener<Location> {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button stopWatchButton;
    private Intent stopWatchIntent;
    private Timer timer;
    private static final int INTERVAL_REFRESH = 10*1000;
    private RunTrackerDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        stopWatchButton = findViewById(R.id.viewStopwatchButton);
        stopWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(stopWatchIntent);
            }
        });
        stopWatchIntent = new Intent(getApplicationContext(),StopwatchActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        db = RunTrackerDB.get(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
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
        mMap = googleMap;

        try {
            fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(getApplicationContext());
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this,this);
            setMapToRefresh();
        } catch (SecurityException ex) {
            Log.e("Run Tracker", ex.getMessage());
        }

    }

    public void onSuccess(Location location) {
        if (location != null) {
            setCurrentLocationMarker(location);
            displayRun();
        }
    }

    private void setCurrentLocationMarker(Location location) {
        if (mMap != null) {
            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(),location.getLongitude()))
                        .zoom(16.5f)
                        .bearing(0)
                        .tilt(25)
                        .build()
                ));
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(),location.getLongitude()))
                        .title("You are here"));
            }
        }
    }
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        setMapToRefresh();
    }

    private void displayRun() {
        if (mMap != null) {
            PolylineOptions polyline = new PolylineOptions();
            ArrayList<Location> list = db.getLocations();
            if (list.size() > 0) {
                for (Location l : list) {
                    LatLng point = new LatLng(l.getLatitude(),l.getLongitude());
                    polyline.add(point);
                }
            }
            polyline.width(10);
            polyline.color(Color.RED);
            mMap.addPolyline(polyline);
        }
    }

    private void updateMap(){

        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(
                    this, this);
        }catch (SecurityException ex) {
            Log.e("Run Tracker", ex.getMessage());
        }


    }

    private void setMapToRefresh(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMap();
                    }
                });
            }
        };
        timer.schedule(task, INTERVAL_REFRESH, INTERVAL_REFRESH);
    }
}
