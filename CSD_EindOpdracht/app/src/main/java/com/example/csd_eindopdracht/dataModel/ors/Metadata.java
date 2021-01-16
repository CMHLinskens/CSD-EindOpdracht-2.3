package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Metadata implements Serializable {

    public String attribution;
    public String service;
    public long timestamp;
    public Query query;
    public Engine engine;

    public Metadata (JSONObject jsonObject){
        try {
            this.attribution = jsonObject.getString("attribution");
            this.service = jsonObject.getString("service");
            this.timestamp = jsonObject.getLong("timestamp");
            this.query = new Query(jsonObject.getJSONObject("query"));
            this.engine = new Engine(jsonObject.getJSONObject("engine"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
