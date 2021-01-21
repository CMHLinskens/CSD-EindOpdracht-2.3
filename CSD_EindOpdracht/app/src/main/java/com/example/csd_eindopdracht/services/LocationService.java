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
import org.osmdroid.util.GeoPoint;

import java.util.Random;

public class LocationService extends Service {
    private static final String LOGTAG = LocationService.class.getName();

    private static LocationManager locationManager = null;
    private static final int LOCATION_INTERVAL = 2500;
    private static final float LOCATION_DISTANCE = 1f;
    LocationListener locationListener = null;
    public static final double DISTANCE_THRESHOLD = 40;
    private static final double COMPLETION_THRESHOLD = 10;

    public static class WayPointReachedEvent {
        WayPoint wayPoint;
        GeoPoint completionPoint;

        WayPointReachedEvent(WayPoint wayPoint, GeoPoint completionPoint) {
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
            WayPoint wp = getWayPointInBounds(location);
            if(wp != null) {
                EventBus.getDefault().post(new WayPointReachedEvent(wp, getRandomCompletionPoint(wp.getLocation())));
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

    /**
     * Calculates a random point within a radius of 40 meters of location
     * @param location center point of circle
     * @return GeoPoint with random point coordinates
     */
    public static GeoPoint getRandomCompletionPoint(GeoPoint location) {
        Random random = new Random();
        int maxOffSetLatitude = 3154653;
        int maxOffSetLongitude = 3984365;

        int rawRandomLatitude = random.nextInt(maxOffSetLatitude) + random.nextInt(maxOffSetLatitude);
        int randomLatitude = rawRandomLatitude > maxOffSetLatitude? maxOffSetLatitude - rawRandomLatitude : rawRandomLatitude;
        double offSetLatitude = randomLatitude / 10000000000.0;

        int rawRandomLongitude = random.nextInt(maxOffSetLongitude) + random.nextInt(maxOffSetLongitude);
        int randomLongitude = rawRandomLongitude > maxOffSetLongitude? maxOffSetLongitude - rawRandomLongitude : rawRandomLongitude;
        double offsetLongitude = randomLongitude / 10000000000.0;
        
        return new GeoPoint(location.getLatitude() + offSetLatitude, location.getLongitude() + offsetLongitude);
    }

    /**
     * Checks if point 1 and 2 are within the DISTANCE_THRESHOLD
     * @param point1 first point
     * @param point2 second point
     * @return result as boolean
     */
    public static boolean checkIfInBounds(GeoPoint point1, GeoPoint point2) {
        return point1.distanceToAsDouble(point2) <= DISTANCE_THRESHOLD;
    }

    /**
     * Checks if point 1 and 2 are within the COMPLETION_THRESHOLD * modifier
     * @param point1 first point
     * @param point2 second point
     * @return result as boolean
     */
    public static boolean checkIfInBounds(GeoPoint point1, GeoPoint point2, int modifier) {
        return point1.distanceToAsDouble(point2) <= (COMPLETION_THRESHOLD * modifier);
    }

    /**
     * Uses LocationManager to retrieve the last known location of the user
     * @return last location info
     */
    public static Location getLastKnownLocation() {
        try{
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            WayPoint wp = getWayPointInBounds(location);
//            if(wp != null)
//                EventBus.getDefault().post(new WayPointReachedEvent(wp, getRandomCompletionPoint(wp.getLocation())));
            return location;
        } catch(SecurityException e){
            Log.e(LOGTAG, "ERROR: in getLastKnownLocation in LocationService.java " + e.getMessage());
        }
        return null;
    }

    /**
     * Check if location in standing inside any of the way points
     * @param location user location
     * @return WayPoint that user is standing in, if no way point found return null
     */
    private static WayPoint getWayPointInBounds(Location location){
        for (WayPoint wp : Data.INSTANCE.getWayPoints()) {
            if (checkIfInBounds(wp.getLocation(), new GeoPoint(location.getLatitude(), location.getLongitude()))) {
                return wp;
            }
        }
        return null;
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
