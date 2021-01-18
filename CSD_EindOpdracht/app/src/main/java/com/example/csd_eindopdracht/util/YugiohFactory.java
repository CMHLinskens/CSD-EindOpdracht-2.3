package com.example.csd_eindopdracht.util;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.collectable.YugiohCollectable;
import com.example.csd_eindopdracht.dataModel.wayPoint.CachePoint;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.dataModel.wayPoint.YugiohCachePoint;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

public class YugiohFactory extends Factory{

    @Override
    public Collectable createCollectable(String name, String imgLink, int level, String id, String description) {
        return new YugiohCollectable(name,  imgLink, level, id, description);
    }

    @Override
    public Collectable createCollectable(JSONObject jsonObject) {
        try {
            return new YugiohCollectable(jsonObject.getString("name"),
                    jsonObject.getJSONArray("card_images").getJSONObject(0).getString("image_url"),
                    jsonObject.getInt("level"),
                    String.valueOf(jsonObject.getInt("id")),
                    jsonObject.getString("desc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CachePoint createCachePoint(String name, GeoPoint location, Collectable collectable) {
        return new YugiohCachePoint(name, location, collectable);
    }

    @Override
    public CachePoint createCachePoint(JSONObject jsonObject) {
        try {
            return new YugiohCachePoint(jsonObject.getString("name"),
                    new GeoPoint(jsonObject.getDouble("longitude"),
                    jsonObject.getDouble("latitude")), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
