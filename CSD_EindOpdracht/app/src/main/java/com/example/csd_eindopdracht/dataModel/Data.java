package com.example.csd_eindopdracht.dataModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Window;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.services.LocationService;
import com.example.csd_eindopdracht.services.dataApiManager.DataApiManager;
import com.example.csd_eindopdracht.services.dataApiManager.YugiohDataAPIManager;
import com.example.csd_eindopdracht.util.Factory;
import com.example.csd_eindopdracht.util.RandomCardListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public enum Data {
    INSTANCE;
    private static final String LOGTAG = Data.class.getName();

    private Factory factory = null;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private List<WayPoint> wayPoints = new ArrayList<>();
    private List<Collectable> inventory = new ArrayList<>();
    private DataApiManager dataApiManager;
    private LocationService.WayPointReachedEvent savedWayPointEvent = null;
    private int points;
    private WayPoint selectedRouteWayPoint = null;

    public Factory getFactory() { return this.factory; }
    public List<WayPoint> getWayPoints() { return this.wayPoints; }
    public List<Collectable> getInventory() { return this.inventory; }
    public LocationService.WayPointReachedEvent getSavedWayPointEvent() { return this.savedWayPointEvent; }
    public void setSavedWayPointEvent(LocationService.WayPointReachedEvent wayPointEvent) { this.savedWayPointEvent = wayPointEvent; }
    public int getPoints() { return this.points; }
    public WayPoint getSelectedRouteWayPoint() { return selectedRouteWayPoint; }
    public void setSelectedRouteWayPoint(WayPoint selectedRouteWayPoint) { this.selectedRouteWayPoint = selectedRouteWayPoint; }

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
        dataApiManager = new YugiohDataAPIManager(factory);

        // Uncomment to reset all saved data.
//        editor.clear().apply();

        try {
            JSONArray wayPointsJsonArray = new JSONArray(getJsonFromAssets(context, "waypoints.json"));

            for(int j = 0; j < wayPointsJsonArray.length(); j++){
                wayPoints.add(factory.createCachePoint(wayPointsJsonArray.getJSONObject(j)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get all collected collectables
        retrieveInventory();
        retrieveSavedPoints();
    }

    /**
     * Adds/subtracts the given amount of points to the points variable
     * And saves it to shared preferences
     * @param points point to add/subtract
     */
    public void addOrSubtractPoints(int points){
        this.points += points;
        editor.putInt("points", this.points).apply();
    }

    /**
     * Retrieves the saved amount of points in shared preferences
     */
    private void retrieveSavedPoints(){
        this.points = sharedPreferences.getInt("points", 0);
    }


    /**
     * Adds new collectable to inventory list and saves to shared preferences
     * @param collectable new obtained collectable
     */
    public void addToInventory(Collectable collectable) {
        inventory.add(0, collectable);

        // Create a list of all the collectables in inventory
        List<String> inventoryIDs = new ArrayList<>();
        for(Collectable c : inventory){
            inventoryIDs.add(c.getId());
        }

        // Convert to json string and save to shared preferences
        String json = new Gson().toJson(inventoryIDs);
        editor.putString("inventory", json);
        editor.apply();
    }

    /**
     * Removes a collectible from the inventory and saves new inventory to shared preferences
     * @param collectable to remove collectable
     */
    public void removeFromInventory(Collectable collectable) {
        if(inventory.contains(collectable)){
            inventory.remove(collectable);

            // Create a list of all the collectables in inventory
            List<String> inventoryIDs = new ArrayList<>();
            for(Collectable c : inventory){
                    inventoryIDs.add(c.getId());
            }

            // Convert to json string and save to shared preferences
            String json = new Gson().toJson(inventoryIDs);
            editor.putString("inventory", json);
            editor.apply();
        } else {
            Log.e(LOGTAG, "ERROR: Trying to remove a non existing card from inventory.");
        }
    }

    /**
     * Retrieves all saved collectables from shared preferences
     */
    private void retrieveInventory(){
        // Get string from shared preferences and convert to string list
        String json = sharedPreferences.getString("inventory", "[]");
        Type listIDType = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> inventoryIDs = new Gson().fromJson(json, listIDType);

        if(inventoryIDs.size() > 0)
            getCardsWithID(inventoryIDs);
        else {
            // If it is the first time the user uses this app fill the inventory with 5 random cards ranging from level 1 to 5
            for(int i = 1; i < 6; i++) {
                getRandomCardWithLevel(i, this::addToInventory);
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

    /**
     * Gets the last inventory spin date
     * @return localDateTime of last spin date
     */
    public DateTime getLastSpinDate() {
        String parsableDateTime = sharedPreferences.getString("lastSpin", "");
        if (!parsableDateTime.equals(""))
            return DateTime.parse(parsableDateTime);
        else
            return null;
    }

    /**
     * Sets the last spin date to the current time
     * and stores it in the shared preferences
     */
    public void setLastSpinDate() {
        sharedPreferences.edit().putString("lastSpin", DateTime.now().toString()).apply();
    }

    /**
     * Retrieves the cards from the database with the given IDs
     * @param IDs cards to retrieve
     */
    public void getCardsWithID(List<String> IDs){
        dataApiManager.getCardsWithID(IDs, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(LOGTAG, "Error: callback in method getCardsWithID in Data.java resulted in failure. " + e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                JSONObject responseJson;
                try{
                    responseJson = new JSONObject(response.body().string());
                    JSONArray responseArray = responseJson.getJSONArray("data");

                    for(int i = 0; i < responseArray.length(); i++){
                        Collectable collectable = factory.createCollectable(responseArray.getJSONObject(i));
                        if(collectable != null)
                            inventory.add(collectable);
                    }
                } catch(JSONException | IOException e){
                    Log.e(LOGTAG, "ERROR: JSON or IO Exception in getCardsWithID in Data.java " + e.getMessage());
                }
            }
        });
    }

    /**
     * Sends a API call to get all the cards with the given level
     * When it received all cards, it randomly picks one and notifies the listener with the new card
     * @param level level of the card to get
     * @param listener listener to notify when its done
     */
    public void getRandomCardWithLevel(int level, RandomCardListener listener){
        dataApiManager.getCardsWithLevel(level, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(LOGTAG, "Error: fail in data callback " + e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                new Thread(() -> {
                    JSONObject responseJson;
                    try{
                        responseJson = new JSONObject(response.body().string());
                        JSONArray responseArray = responseJson.getJSONArray("data");

                        List<Collectable> cardList = new ArrayList<>();
                        for(int i = 0; i < responseArray.length(); i++){
                            Collectable collectable = factory.createCollectable(responseArray.getJSONObject(i));
                            if(collectable != null)
                                cardList.add(collectable);
                        }

                        Collectable randomCard = cardList.get(new Random().nextInt(cardList.size()));
                        listener.onRandomCardReceived(randomCard);
                    } catch(JSONException | IOException e){
                        Log.e(LOGTAG, "ERROR: JSON or IO Exception in getRandomCardWithLevel in Data.java " + e.getMessage());
                    }
                }).start();
            }
        });
    }
}
