package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Step implements Serializable {
    public double distance;
    public double duration;
    public int type;
    public String instruction;
    public String name;
    public int[] wayPoints;

    public Step(JSONObject jsonObject) {
        try {
            this.distance = jsonObject.getDouble("distance");
            this.duration = jsonObject.getDouble("duration");
            this.type = jsonObject.getInt("type");
            this.instruction = jsonObject.getString("instruction");
            this.name = jsonObject.getString("name");
            this.wayPoints = jsonArrayToArray(jsonObject.getJSONArray("way_points"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int[] jsonArrayToArray(JSONArray array){
        int[] way_points = new int[2];
        for (int jsonArrayIndex = 0; jsonArrayIndex < array.length(); jsonArrayIndex++){
            try {
                way_points[jsonArrayIndex] = array.getInt(jsonArrayIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return way_points;
    }
}
