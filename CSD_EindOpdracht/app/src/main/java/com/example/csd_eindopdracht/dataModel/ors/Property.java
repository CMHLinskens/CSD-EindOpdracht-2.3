package com.example.csd_eindopdracht.dataModel.ors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Property implements Serializable {
    public ArrayList<Segment> segments;
    public Summary summary;
    public int[] wayPoints;

    public Property(JSONObject jsonObject){
        try {
            this.segments = jsonArrayToList(jsonObject.getJSONArray("segments"));
            this.summary = new Summary(jsonObject.getJSONObject("summary"));
            this.wayPoints = jsonArrayToArray(jsonObject.getJSONArray("way_points"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Segment> jsonArrayToList(JSONArray array){
        ArrayList<Segment> list = new ArrayList<>();
        for (int arrayIndex = 0; arrayIndex < array.length(); arrayIndex++){
            try {
                list.add(new Segment(array.getJSONObject(arrayIndex)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public int[] jsonArrayToArray(JSONArray array){
        final int[] wayPointsArray = new int[2];
        for (int jsonArrayIndex = 0; jsonArrayIndex < array.length(); jsonArrayIndex++){
            try {
                wayPointsArray[jsonArrayIndex] = array.getInt(jsonArrayIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return wayPointsArray;
    }
}
