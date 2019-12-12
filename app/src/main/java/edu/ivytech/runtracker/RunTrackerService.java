package edu.ivytech.runtracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import edu.ivytech.runtracker.database.RunTrackerDB;

public class RunTrackerService extends Service {
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int FASTEST_UPDATE_INTERVAL= 2000;
    private RunTrackerDB db;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = RunTrackerDB.get(getApplicationContext());
        fusedLocationProviderClient = LocationServices.
                getFusedLocationProviderClient(getApplicationContext());
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.myLooper());

        } catch (SecurityException ex) {
            Log.e("Run Tracker Service",ex.getMessage());
        }

    }

    @Override
    public void onDestroy() {
    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    public void onLocationChanged(Location location) {
        if(location != null) {
            db.insertLocation(location);
        }
    }
}
