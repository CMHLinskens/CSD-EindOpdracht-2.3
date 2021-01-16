package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Segment implements Serializable {

    public double distance;
    public double duration;
    public ArrayList<Step> steps;

    public Segment (JSONObject jsonObject){
        try {
            this.distance = jsonObject.getDouble("distance");
            this.duration = jsonObject.getDouble("duration");
            this.steps = jsonArrayToList(jsonObject.getJSONArray("steps"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Step> jsonArrayToList(JSONArray array){
        ArrayList<Step> list = new ArrayList<>();
        for (int arrayIndex = 0; arrayIndex < array.length(); arrayIndex++){
            try {
                list.add(new Step(array.getJSONObject(arrayIndex)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
