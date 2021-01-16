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
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private List<Collectable> collectables = new ArrayList<>();
    private List<WayPoint> wayPoints = new ArrayList<>();
    private List<Collectable> inventory = new ArrayList<>();

    public Factory getFactory() { return this.factory; }
    public List<Collectable> getCollectables() { return this.collectables; }
    public List<WayPoint> getWayPoints() { return this.wayPoints; }
    public List<Collectable> getInventory() { return this.inventory; }

    /**
     * Method called on start up of app.
     * Collects all data from json files and shared preferences
     * @param context application context
     * @param factory the factory to build collectables and way points with
     */
    public void retrieveAllData(Context context, Factory factory) {
        this.factory = factory;
        this.sharedPreferences = context.getSharedPreferences(Data.class.getName(), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

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

        // Get all collected collectables
        retrieveInventory();
    }

    /**
     * Adds new collectable to inventory list and saves to shared preferences
     * @param collectable new obtained collectable
     */
    public void addToInventory(Collectable collectable) {
        inventory.add(collectable);

        // Create a list of all the collectables in inventory
        List<String> inventoryNames = new ArrayList<>();
        for(Collectable c : inventory){
            inventoryNames.add(c.getName());
        }

        // Convert to json string and save to shared preferences
        String json = new Gson().toJson(inventoryNames);
        editor.putString("inventory", json);
        editor.apply();
    }

    /**
     * Retrieves all saved collectables from shared preferences
     */
    private void retrieveInventory(){
        // Get string from shared preferences and convert to string list
        String json = sharedPreferences.getString("inventory", "[]");
        Type listNamesType = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> inventoryNames = new Gson().fromJson(json, listNamesType);

        // Search for matching collectable and add to inventory
        for(String name : inventoryNames){
            for(Collectable c : collectables){
                if(name.equals(c.getName())){
                    inventory.add(c);
                }
            }
        }
    }

    /**
     * Reads json file from assets folder
     * @param context application context
     * @param fileName name of the file to read from
     * @return json file content in a string
     */
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
}
