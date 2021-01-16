package com.example.csd_eindopdracht.dataModel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonToken;
import android.util.Log;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.util.Factory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public enum Data {
    INSTANCE;
    private static final String LOGTAG = Data.class.getName();

    private Factory factory = null;
    private List<Collectable> collectables = new ArrayList<>();
    private List<WayPoint> wayPoints = new ArrayList<>();
    private List<Collectable> inventory = new ArrayList<>();

    public void retrieveAllData(Context context, Factory factory) {
        this.factory = factory;

        try {
            // Make JSONArrays from the json strings
            JSONArray collectablesJsonArray = new JSONArray(getJsonFromAssets(context, "collectables.json"));
            JSONArray wayPointsJsonArray = new JSONArray(getJsonFromAssets(context, "waypoints.json"));

            for(int i = 0; i < collectablesJsonArray.length(); i++){
                collectables.add(factory.createCollectable(collectablesJsonArray.getJSONObject(i)));
            }

            for(int j = 0; j < wayPointsJsonArray.length(); j++){
                wayPoints.add(factory.createCachePoint(wayPointsJsonArray.getJSONObject(j)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (Collectable c : collectables){
            Log.d(LOGTAG, "Collectable: " + c.getName() + " " + c.getImgLink());
        }

        for(WayPoint w : wayPoints){
            Log.d(LOGTAG, "WayPoint: " + w.getName() + " " + w.getLocation());
        }
    }

    private String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream inputStream = context.getAssets().open(fileName);

            int fileSize = inputStream.available();
            byte[] buffer = new byte[fileSize];
            inputStream.read(buffer);
            inputStream.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public List<Collectable> getCollectables() {
        return collectables;
    }
}
