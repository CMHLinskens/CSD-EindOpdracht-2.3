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
import android.view.View;

import com.example.csd_eindopdracht.BuildConfig;
import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.services.LocationService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

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

        // Retrieve all data
        Data.INSTANCE.retrieveAllData(getApplicationContext(), factory);

        // Create a notification channel for Android 8.1 and above
        createNotificationChannel();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, factory.createMapFragment()).commit();
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

    @Override
    protected void onStop() {
        super.onStop();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for(Fragment f : fragmentList)
            if(f instanceof MapFragment)
                if(!EventBus.getDefault().isRegistered(f))
                    EventBus.getDefault().register(f);
    }
}