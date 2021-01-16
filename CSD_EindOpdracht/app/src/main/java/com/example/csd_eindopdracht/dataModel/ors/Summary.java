package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Summary implements Serializable {

    public double distance;
    public double duration;

    public Summary (JSONObject jsonObject){
        try {
            this.distance = jsonObject.getDouble("distance");
            this.duration = jsonObject.getDouble("duration");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
