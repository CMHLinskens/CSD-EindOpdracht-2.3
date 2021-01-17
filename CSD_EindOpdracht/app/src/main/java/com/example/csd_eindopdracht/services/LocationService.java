package com.example.csd_eindopdracht.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.util.GeoPoint;

public class LocationService extends Service {
    private static final String LOGTAG = LocationService.class.getName();

    private LocationManager locationManager = null;
    private static final int LOCATION_INTERVAL = 2500;
    private static final float LOCATION_DISTANCE = 10f;
    LocationListener locationListener = null;
    private static final double DISTANCE_THRESHOLD = 40;

    public static class WayPointReachedEvent {
        WayPoint wayPoint;
        GeoPoint completionPoint;

        WayPointReachedEvent(WayPoint wayPoint, GeoPoint completionPoint){
            this.wayPoint = wayPoint;
            this.completionPoint = completionPoint;
        }

        public WayPoint getWayPoint() {
            return wayPoint;
        }
        public GeoPoint getCompletionPoint() {
            return completionPoint;
        }
    }

    private class LocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            EventBus.getDefault().post(location);
            for(WayPoint wp : Data.INSTANCE.getWayPoints()){
                double distance = wp.getLocation().distanceToAsDouble(new GeoPoint(location.getLatitude(), location.getLongitude()));
                if(distance <= DISTANCE_THRESHOLD) {
                    EventBus.getDefault().post(new WayPointReachedEvent(wp, getRandomCompletionPoint(wp.getLocation())));
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Ignore status changed
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.e(LOGTAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Log.e(LOGTAG, "onProviderDisabled: " + provider);
        }
    }

    private GeoPoint getRandomCompletionPoint(GeoPoint location) {
        // TODO calculate a random point within a radius of 40 meters of location
        return location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Start notification for foreground service
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "notifychannelid")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Location Service Running ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        startForeground(1, notification);

        // initialise manager and listener
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener();

        // request updates from location manager
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
                    LOCATION_DISTANCE, locationListener
            );

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
                    LOCATION_DISTANCE, locationListener
            );
        } catch (SecurityException e) {
            Log.i(LOGTAG, "Failed to request location update", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}
