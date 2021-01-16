package com.example.csd_eindopdracht.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.csd_eindopdracht.BuildConfig;
import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.services.LocationService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.services.OpenRouteServiceManager;
import com.example.csd_eindopdracht.ui.fragment.MapFragment;
import com.example.csd_eindopdracht.util.Factory;
import com.example.csd_eindopdracht.util.YugiohFactory;

public class MainActivity extends AppCompatActivity {
    private static final String LOGTAG = MainActivity.class.getName();
    private static final Factory factory = new YugiohFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set User Agent Value for OSM map view
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        Data.INSTANCE.retrieveAllData(getApplicationContext(), factory);

        // Create a notification channel for Android 8.1 and above
        createNotificationChannel();

        // Request for location permission
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
        });

        // Start locationService as a ForegroundService
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startForegroundService(locationServiceIntent);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, factory.createMapFragment()).commit();
        }
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

    /**
     * Create a notification channel for Android 8.1 and above
     * in order to avoid bad notification for startForeground
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notifychannel";
            String description = "notifydescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifychannelid", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}