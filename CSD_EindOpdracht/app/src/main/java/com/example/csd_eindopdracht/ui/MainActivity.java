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

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, factory.createMapFragment()).commit();
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