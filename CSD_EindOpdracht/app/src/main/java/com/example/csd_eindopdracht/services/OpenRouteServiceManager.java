package com.example.csd_eindopdracht.services;

import android.location.Location;
import android.util.Log;

import com.example.csd_eindopdracht.dataModel.ors.TravelType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.util.GeoPoint;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OpenRouteServiceManager {
    private static final String LOGTAG = OpenRouteServiceManager.class.getName();

    private static final String APIKey = "5b3ce3597851110001cf62486f9ca76c77ba4d96938e295eb9e37000";
    private final OkHttpClient client;
    private final GeoPoint myLocation = new GeoPoint(0, 0);

    public OpenRouteServiceManager() {
        client = new OkHttpClient();

        // Subscribe to EventBus
        EventBus.getDefault().register(this);
    }

    /**
     * Sends an API call to ORS and executes callback with the retrieved route data
     * @param start start point of route
     * @param end end point of route
     * @param travelType way of transport
     * @param callback callback to execute on reply
     */
    public void getRoute(GeoPoint start, GeoPoint end, TravelType travelType, Callback callback) {
        final String url = "https://api.openrouteservice.org/v2/directions/" +
                TravelType.getTravelType(travelType) +
                "?api_key=" + APIKey +
                "&start=" + start.getLongitude() + "," + start.getLatitude() +
                "&end=" + end.getLongitude() + "," + end.getLatitude();

        final Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     * Method triggered by EventBus when new location is received
     * @param location new location data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(Location location){
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
    }
}
