package com.example.csd_eindopdracht.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.csd_eindopdracht.BuildConfig;
import com.example.csd_eindopdracht.services.LocationService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.config.Configuration;

import java.util.ArrayList;

import com.example.csd_eindopdracht.R;

public class MainActivity extends AppCompatActivity {
    private static final String LOGTAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Request for location permission
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
        });

        // Subscribe to EventBus
        EventBus.getDefault().register(this);
        // Start locationService as an ForegroundService
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startForegroundService(locationServiceIntent);
    }

    /**
     * Method triggered by EventBus when new location is received
     * @param location new location data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(Location location){
        Log.d(LOGTAG, "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
    }

    /**
     Checks for each permission if it already has been granted
     if not ask the user for this permission
     @param permissions String array with permissions to ask
     */
    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}