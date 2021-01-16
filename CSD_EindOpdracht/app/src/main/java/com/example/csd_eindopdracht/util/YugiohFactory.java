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
    public Collectable createCollectable(String name, String imgLink) {
        return new YugiohCollectable(name,  imgLink);
    }

    @Override
    public Collectable createCollectable(JSONObject jsonObject) {
        try {
            return new YugiohCollectable(jsonObject.getString("name"), jsonObject.getString("imgLink"));
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
